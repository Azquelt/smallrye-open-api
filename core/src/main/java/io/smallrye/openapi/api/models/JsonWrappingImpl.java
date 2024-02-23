package io.smallrye.openapi.api.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.smallrye.openapi.runtime.io.JsonUtil;
import io.smallrye.openapi.runtime.io.ObjectWriter;

/**
 * Base implementation for extensible model objects which wrap an arbitrary JSON node
 */
public abstract class JsonWrappingImpl implements ModelImpl {

    /**
     * JsonNodeFactory which doesn't normalize decimals
     */
    protected static final JsonNodeFactory factory = new JsonNodeFactory(true);

    protected final ObjectNode node;

    protected JsonWrappingImpl(ObjectNode node) {
        this.node = node;
    }

    /**
     * Merge all properties from another JSON model object into this one
     * <p>
     * Usually this method will return {@code this}, but it may return {@code other} or a new object.
     *
     * @param other the other JSON object
     * @return the merged object
     */
    public JsonWrappingImpl mergeFrom(JsonWrappingImpl other) {
        node.setAll(other.node);
        return this;
    }

    protected <T> void setProperty(String propertyName, T value, JsonWriter<T> writer) {
        if (value == null) {
            node.remove(propertyName);
        } else {
            node.replace(propertyName, writer.toNode(value));
        }
    }

    protected <T> T getProperty(String propertyName, JsonReader<T> reader) {
        JsonNode propertyNode = node.get(propertyName);
        return propertyNode == null ? null : reader.fromNode(propertyNode);
    }

    protected <T> List<T> getListProperty(String propertyName, JsonReader<T> reader) {
        JsonNode propertyNode = node.get(propertyName);
        if (propertyNode == null) {
            return null;
        }
        if (!propertyNode.isArray()) {
            return null;
        }
        List<T> result = new ArrayList<>();
        for (JsonNode elementNode : propertyNode) {
            result.add(reader.fromNode(elementNode));
        }
        return Collections.unmodifiableList(result);
    }

    protected <T> void setListProperty(String propertyName, List<T> value, JsonWriter<T> writer) {
        if (value == null) {
            node.remove(propertyName);
        } else {
            ArrayNode propertyNode = node.arrayNode(value.size());
            for (T item : value) {
                propertyNode.add(writer.toNode(item));
            }
            node.replace(propertyName, propertyNode);
        }
    }

    protected <T> void addToListProperty(String propertyName, T value, JsonWriter<T> writer) {
        JsonNode propertyNode = node.get(propertyName);
        ArrayNode arrayPropertyNode;
        if (propertyNode == null || !propertyNode.isArray()) {
            arrayPropertyNode = node.putArray(propertyName);
        } else {
            arrayPropertyNode = (ArrayNode) propertyNode;
        }

        if (value == null) {
            arrayPropertyNode.addNull();
        } else {
            arrayPropertyNode.add(writer.toNode(value));
        }
    }

    protected <T> void removeFromListProperty(String propertyName, T toRemove, JsonReader<T> reader) {
        JsonNode propertyNode = node.get(propertyName);
        if (propertyNode == null || !propertyNode.isArray()) {
            // Property is not an array, it cannot contain the value
            return;
        }

        ArrayNode arrayPropertyNode = (ArrayNode) propertyNode;

        // Iterate through the existing list, converting each element to a model object
        // If we find a model object equal to toRemove, remove the corresponding JsonNode element from the list
        for (Iterator<JsonNode> i = arrayPropertyNode.iterator(); i.hasNext();) {
            JsonNode elementNode = i.next();
            T elementValue = reader.fromNode(elementNode);
            if (Objects.equals(toRemove, elementValue)) {
                i.remove();
                break;
            }
        }
    }

    protected <T> void setMapProperty(String propertyName, Map<String, T> value, JsonWriter<T> writer) {
        if (value == null) {
            node.remove(propertyName);
        } else {
            ObjectNode propertyNode = node.putObject(propertyName);
            for (Entry<String, T> element : value.entrySet()) {
                if (element.getValue() == null) {
                    propertyNode.putNull(element.getKey());
                } else {
                    propertyNode.set(element.getKey(), writer.toNode(element.getValue()));
                }
            }
        }
    }

    protected <T> Map<String, T> getMapProperty(String propertyName, JsonReader<T> reader) {
        JsonNode propertyNode = node.get(propertyName);
        if (propertyNode == null) {
            return null;
        }
        if (!propertyNode.isObject()) {
            return null;
        }

        Map<String, T> result = new LinkedHashMap<>(propertyNode.size());
        for (Iterator<Entry<String, JsonNode>> i = propertyNode.fields(); i.hasNext();) {
            Entry<String, JsonNode> element = i.next();
            result.put(element.getKey(), reader.fromNode(element.getValue()));
        }

        return Collections.unmodifiableMap(result);
    }

    protected <T> void addToMapProperty(String propertyName, String key, T value, JsonWriter<T> writer) {
        JsonNode propertyNode = node.get(propertyName);
        ObjectNode objectPropertyNode;
        if (propertyNode == null || !propertyNode.isObject()) {
            objectPropertyNode = node.putObject(propertyName);
        } else {
            objectPropertyNode = (ObjectNode) propertyNode;
        }

        if (value == null) {
            objectPropertyNode.putNull(key);
        } else {
            objectPropertyNode.replace(key, writer.toNode(value));
        }
    }

    protected <T> void removeFromMapProperty(String propertyName, String key) {
        JsonNode propertyNode = node.get(propertyName);
        if (propertyNode == null || !propertyNode.isObject()) {
            // Property is not an object, it cannot contain the key
            return;
        }

        ObjectNode objectPropertyNode = (ObjectNode) propertyNode;
        objectPropertyNode.remove(propertyName);
    }

    protected static interface JsonWriter<T> {
        /**
         * Converts a value to a JsonNode
         *
         * @param value the value, must not be {@code null}
         * @return the new JsonNode
         */
        public JsonNode toNode(T value);
    }

    protected static interface JsonReader<T> {
        /**
         * Converts a JsonNode to a value
         *
         * @param node the JsonNode to convert, must not be {@code null}, but may be {@link NullNode}
         * @return the value, or {@code null} if the JsonNode is not of the expected type
         */
        public T fromNode(JsonNode node);
    }

    public static interface JsonConverter<T> extends JsonReader<T>, JsonWriter<T> {
    }

    protected static final JsonConverter<String> STRING_CONVERTER = new JsonConverter<String>() {

        @Override
        public JsonNode toNode(String value) {
            return JsonNodeFactory.instance.textNode(value);
        }

        @Override
        public String fromNode(JsonNode node) {
            return node.isTextual() ? node.textValue() : null;
        }
    };

    protected static final JsonConverter<Integer> INT_CONVERTER = new JsonConverter<Integer>() {

        @Override
        public JsonNode toNode(Integer value) {
            return JsonNodeFactory.instance.numberNode(value);
        }

        @Override
        public Integer fromNode(JsonNode node) {
            return node.canConvertToInt() ? node.asInt() : null;
        }
    };

    protected static final JsonConverter<BigDecimal> NUMBER_CONVERTER = new JsonConverter<BigDecimal>() {

        @Override
        public JsonNode toNode(BigDecimal value) {
            return factory.numberNode(value);
        }

        @Override
        public BigDecimal fromNode(JsonNode node) {
            return node.isNumber() ? node.decimalValue() : null;
        }
    };

    protected static final JsonConverter<Boolean> BOOLEAN_CONVERTER = new JsonConverter<Boolean>() {

        @Override
        public JsonNode toNode(Boolean value) {
            return JsonNodeFactory.instance.booleanNode(value);
        }

        @Override
        public Boolean fromNode(JsonNode node) {
            return node.isBoolean() ? node.booleanValue() : null;
        }
    };

    protected static final JsonConverter<Object> OBJECT_CONVERTER = new JsonConverter<Object>() {

        @Override
        public JsonNode toNode(Object value) {
            return ObjectWriter.convertObjectToNode(JsonNodeFactory.instance, value);
        }

        @Override
        public Object fromNode(JsonNode node) {
            return JsonUtil.readObject(node);
        }
    };

    //    @Override
    //    public int hashCode() {
    //        return Objects.hash(node);
    //    }
    //
    //    @Override
    //    public boolean equals(Object obj) {
    //        if (this == obj)
    //            return true;
    //        if (obj == null)
    //            return false;
    //        if (getClass() != obj.getClass())
    //            return false;
    //        JsonWrappingImpl other = (JsonWrappingImpl) obj;
    //        return Objects.equals(node, other.node);
    //    }

}
