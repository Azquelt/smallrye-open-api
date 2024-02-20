package io.smallrye.openapi.runtime.io;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ObjectWriter {

    private ObjectWriter() {
    }

    /**
     * Writes an array of strings to the parent node.
     *
     * @param parent the parent json node
     * @param models list of Strings
     * @param propertyName the name of the node
     */
    public static void writeStringArray(ObjectNode parent, List<String> models, String propertyName) {
        if (models == null) {
            return;
        }
        ArrayNode node = parent.putArray(propertyName);
        for (String model : models) {
            node.add(model);
        }
    }

    /**
     * Writes an array of objects to the parent node.
     *
     * @param parent the parent json node
     * @param models list of objects
     * @param propertyName the name of the node
     */
    public static void writeObjectArray(ObjectNode parent, List<Object> models, String propertyName) {
        if (models == null) {
            return;
        }
        ArrayNode node = parent.putArray(propertyName);
        for (Object model : models) {
            addObject(node, model);
        }
    }

    /**
     * Writes a map of strings to the parent node.
     *
     * @param parent the parent json node
     * @param models map of strings
     * @param propertyName name of the node
     */
    public static void writeStringMap(ObjectNode parent, Map<String, String> models, String propertyName) {
        if (models == null) {
            return;
        }
        ObjectNode node = parent.putObject(propertyName);
        for (Map.Entry<String, String> entry : models.entrySet()) {
            node.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Write an object to json
     *
     * @param node the json node
     * @param key key
     * @param value value
     */
    public static void writeObject(ObjectNode node, String key, Object value) {
        if (value == null) {
            return;
        }
        node.set(key, convertObjectToNode(node, value));
    }

    /**
     * Add an object into a JSON array
     * 
     * @param node the array
     * @param value the object to add
     */
    public static void addObject(ArrayNode node, Object value) {
        node.add(convertObjectToNode(node, value));
    }
    
    /**
     * Convert an object to a JsonNode
     * 
     * @param nodeCreator the factory to use to create the new JsonNode
     * @param value the object to convert
     * @return the JSON representation of the object
     */
    public static JsonNode convertObjectToNode(JsonNodeCreator nodeCreator, Object value) {
        JsonNode result;
        if (value instanceof String) {
            result = nodeCreator.textNode((String) value);
        } else if (value instanceof JsonNode) {
            result = (JsonNode) value;
        } else if (value instanceof BigDecimal) {
            result = nodeCreator.numberNode((BigDecimal) value);
        } else if (value instanceof BigInteger) {
            result = nodeCreator.numberNode((BigInteger) value);
        } else if (value instanceof Boolean) {
            result = nodeCreator.booleanNode((Boolean) value);
        } else if (value instanceof Double) {
            result = nodeCreator.numberNode((Double) value);
        } else if (value instanceof Float) {
            result = nodeCreator.numberNode((Float) value);
        } else if (value instanceof Integer) {
            result = nodeCreator.numberNode((Integer) value);
        } else if (value instanceof Long) {
            result = nodeCreator.numberNode((Long) value);
        } else if (value instanceof List) {
            ArrayNode array = nodeCreator.arrayNode(((List<?>) value).size());
            for (Object valueItem : List.class.cast(value)) {
                addObject(array, valueItem);
            }
            result = array;
        } else if (value instanceof Map) {
            ObjectNode objNode = nodeCreator.objectNode();
            @SuppressWarnings("unchecked")
            Map<String, Object> values = (Map<String, Object>) value;
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                String propertyName = entry.getKey();
                writeObject(objNode, propertyName, entry.getValue());
            }
            result = objNode;
        } else {
            result = nodeCreator.nullNode();
        }
        return result;
    }
}
