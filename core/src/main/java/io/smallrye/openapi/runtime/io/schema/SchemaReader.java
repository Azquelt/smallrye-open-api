package io.smallrye.openapi.runtime.io.schema;

import static io.smallrye.openapi.runtime.io.schema.DataType.listOf;
import static io.smallrye.openapi.runtime.io.schema.DataType.mapOf;
import static io.smallrye.openapi.runtime.io.schema.DataType.type;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROPERTIES_DATA_TYPES;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_NAME;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_TYPE;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.eclipse.microprofile.openapi.models.ExternalDocumentation;
import org.eclipse.microprofile.openapi.models.media.Discriminator;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.XML;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;

import io.smallrye.openapi.api.models.media.SchemaImpl;
import io.smallrye.openapi.runtime.io.IoLogging;
import io.smallrye.openapi.runtime.io.JsonUtil;
import io.smallrye.openapi.runtime.io.discriminator.DiscriminatorReader;
import io.smallrye.openapi.runtime.io.externaldocs.ExternalDocsReader;
import io.smallrye.openapi.runtime.io.xml.XmlReader;
import io.smallrye.openapi.runtime.scanner.spi.AnnotationScannerContext;
import io.smallrye.openapi.runtime.util.Annotations;
import io.smallrye.openapi.runtime.util.JandexUtil;

/**
 * Reading the Schema annotation
 *
 * @see <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#schemaObject">schemaObject</a>
 *
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 * @author Eric Wittmann (eric.wittmann@gmail.com)
 */
public class SchemaReader {

    private SchemaReader() {
    }

    /**
     * Reads a map of Schema annotations.
     *
     * @param context the scanner context
     * @param annotationValue map of {@literal @}Schema annotations
     * @return Map of Schema models
     */
    public static Map<String, Schema> readSchemas(final AnnotationScannerContext context,
            final AnnotationValue annotationValue) {
        if (annotationValue == null) {
            return null;
        }
        IoLogging.logger.annotationsMap("@Schema");
        Map<String, Schema> map = new LinkedHashMap<>();
        AnnotationInstance[] nestedArray = annotationValue.asNestedArray();
        for (AnnotationInstance nested : nestedArray) {
            String name = Annotations.value(nested, SchemaConstant.PROP_NAME);

            if (name == null && JandexUtil.isRef(nested)) {
                name = JandexUtil.nameFromRef(nested);
            }

            /*
             * The name is REQUIRED when the schema is defined within
             * {@link org.eclipse.microprofile.openapi.annotations.Components}.
             */
            if (name != null) {
                map.put(name, SchemaFactory.readSchema(context, new SchemaImpl(name), nested, Collections.emptyMap()));
            }
        }
        return map;
    }

    /**
     * Reads a {@link Schema} OpenAPI node.
     *
     * @param node json node
     * @return Schema model
     */
    public static Schema readSchema(final JsonNode node) {
        IoLogging.logger.singleJsonObject("Schema");
        if (node == null) {
            return null;
        } else if (node.isObject()) {
            String name = getName(node);
            SchemaImpl schema = new SchemaImpl(name);
            String dialect = JsonUtil.stringProperty(node, SchemaConstant.PROP_SCHEMA_DIALECT);
            if (dialect == null || dialect.equals(SchemaConstant.DIALECT_OAS31)
                    || dialect.equals(SchemaConstant.DIALECT_JSON_2020_12)) {
                populateSchemaObject(schema, (ObjectNode) node);
            } else {
                schema.getDataMap().putAll((Map<? extends String, ? extends Object>) readJson((ObjectNode) node));
            }
            return schema;
        } else if (node.isBoolean()) {
            return SchemaImpl.ofBoolean(node.booleanValue());
        } else {
            return null;
        }
    }

    private static String getName(JsonNode node) {
        JsonNode nameNode = node.get(PROP_NAME);
        if (nameNode != null && nameNode.isTextual()) {
            String result = nameNode.asText();
            return result;
        }
        return null;
    }

    private static void populateSchemaObject(SchemaImpl schema, ObjectNode node) {

        Map<String, Object> dataMap = schema.getDataMap();

        // Special handling for type since it can be an array or a string and we want to convert
        JsonNode typeNode = node.get(PROP_TYPE);
        if (typeNode != null) {
            if (typeNode.isTextual()) {
                ArrayList<Object> typeList = new ArrayList<>();
                typeList.add(readJson(typeNode, type(Schema.SchemaType.class)));
                dataMap.put(PROP_TYPE, typeList);
            } else {
                dataMap.put(PROP_TYPE, readJson(typeNode, listOf(type(Schema.SchemaType.class))));
            }
        }

        // Read known fields
        for (Map.Entry<String, DataType> entry : SchemaConstant.PROPERTIES_DATA_TYPES.entrySet()) {
            String key = entry.getKey();
            DataType type = entry.getValue();
            JsonNode fieldNode = node.get(key);
            if (fieldNode != null) {
                dataMap.put(key, readJson(fieldNode, type));
            }
        }

        // Read unknown fields
        for (Iterator<Entry<String, JsonNode>> i = node.fields(); i.hasNext();) {
            Entry<String, JsonNode> entry = i.next();
            String name = entry.getKey();
            JsonNode fieldNode = entry.getValue();
            if (!PROPERTIES_DATA_TYPES.containsKey(name) && !name.equals(PROP_TYPE) && !name.equals(PROP_NAME)) {
                dataMap.put(name, readJson(fieldNode));
            }
        }
    }

    /**
     * Reads the {@link Schema} OpenAPI nodes.
     *
     * @param node map of schema json nodes
     * @return Map of Schema model
     */
    public static Optional<Map<String, Schema>> readSchemas(final JsonNode node) {
        if (node != null && node.isObject()) {
            Map<String, Schema> models = new LinkedHashMap<>();
            for (Iterator<String> fieldNames = node.fieldNames(); fieldNames.hasNext();) {
                String fieldName = fieldNames.next();
                JsonNode childNode = node.get(fieldName);
                models.put(fieldName, readSchema(childNode));
            }
            return Optional.of(models);
        }
        return Optional.empty();
    }

    private static Object readJson(JsonNode node, DataType desiredType) {
        if (node.isObject() && desiredType.type == DataType.Type.MAP) {
            Map<String, Object> result = new HashMap<>();
            ObjectNode object = (ObjectNode) node;
            for (Iterator<Entry<String, JsonNode>> i = object.fields(); i.hasNext();) {
                Entry<String, JsonNode> type = (Entry<String, JsonNode>) i.next();
                result.put(type.getKey(), readJson(type.getValue(), desiredType.content));
            }
            return result;
        } else if (node.isArray() && desiredType.type == DataType.Type.LIST) {
            List<Object> result = new ArrayList<>();
            ArrayNode array = (ArrayNode) node;
            for (JsonNode element : array) {
                result.add(readJson(element, desiredType.content));
            }
            return result;
        } else if (desiredType.type == DataType.Type.OBJECT) {
            return readValue(node, desiredType.clazz);
        } else {
            return readJson(node);
        }
    }

    /**
     * Convert JSON value node to an object when we have a desired type
     * <p>
     * The JSON value will be converted to the desired type if possible or returned as its native type if not.
     *
     * @param node the JSON node
     * @param desiredType the type that we want to be returned
     * @return an object which represents the JSON node, which may or may not be of the desired type
     */
    @SuppressWarnings("unchecked")
    private static Object readValue(JsonNode node, Class<?> desiredType) {
        if (desiredType == String.class) {
            return node.asText();
        }
        if (desiredType == Integer.class && node.canConvertToInt()) {
            return node.asInt();
        }
        if (desiredType == BigInteger.class && node.canConvertToExactIntegral()) {
            return node.bigIntegerValue();
        }
        if (desiredType == Long.class && node.canConvertToLong()) {
            return node.asLong();
        }
        if (desiredType == BigDecimal.class && node.isNumber()) {
            return node.decimalValue();
        }
        if (Enum.class.isAssignableFrom(desiredType)) {
            try {
                return Enum.valueOf(desiredType.asSubclass(Enum.class), node.asText().toUpperCase());
            } catch (IllegalArgumentException e) {
                // No matching enum instance, handle later in generic case
            }
        }
        if (node.isObject()) {
            if (desiredType == Schema.class) {
                return readSchema(node);
            }
            if (desiredType == XML.class) {
                return XmlReader.readXML(node);
            }
            if (desiredType == ExternalDocumentation.class) {
                return ExternalDocsReader.readExternalDocs(node);
            }
            if (desiredType == Discriminator.class) {
                return DiscriminatorReader.readDiscriminator(node);
            }
        }
        return readJson(node);
    }

    /**
     * Convert a JsonNode to its natural Java type
     *
     * @param node the JSON node
     * @return a java object, may be {@code null} if {@code node} has type {@code MISSING} or {@code NULL}
     */
    private static Object readJson(JsonNode node) {
        switch (node.getNodeType()) {
            case STRING:
            case BINARY:
                return node.textValue();
            case NUMBER:
                if (node.canConvertToExactIntegral()) {
                    if (node.canConvertToInt())
                        return node.intValue();
                    if (node.canConvertToLong())
                        return node.longValue();
                    return node.bigIntegerValue();
                }
                return node.decimalValue();
            case BOOLEAN:
                return node.booleanValue();
            case NULL:
            case MISSING:
                return null;
            case ARRAY:
                return readJson(node, listOf(type(Object.class)));
            case OBJECT:
                return readJson(node, mapOf(type(Object.class)));
            case POJO:
                return ((POJONode) node).getPojo();
            default:
                throw new IllegalArgumentException("Unknown JSON type: " + node.getNodeType());
        }
    }
}
