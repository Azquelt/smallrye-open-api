package io.smallrye.openapi.runtime.io.schema;

import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_DESCRIPTION;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_TITLE;

import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.openapi.models.ExternalDocumentation;
import org.eclipse.microprofile.openapi.models.media.Discriminator;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.XML;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.smallrye.openapi.api.models.media.SchemaImpl;
import io.smallrye.openapi.runtime.io.ObjectWriter;
import io.smallrye.openapi.runtime.io.Referenceable;
import io.smallrye.openapi.runtime.io.components.ComponentsConstant;
import io.smallrye.openapi.runtime.io.discriminator.DiscriminatorWriter;
import io.smallrye.openapi.runtime.io.externaldocs.ExternalDocsWriter;
import io.smallrye.openapi.runtime.io.xml.XmlWriter;

/**
 * Writing the Schema to json
 *
 * @see <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#schemaObject">schemaObject</a>
 *
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 * @author Eric Wittmann (eric.wittmann@gmail.com)
 */
public class SchemaWriter {

    private SchemaWriter() {
    }

    /**
     * Writes a map of {@link Schema} to the JSON tree.
     *
     * @param parent the parent json node
     * @param schemas map of Schema models
     */
    public static void writeSchemas(ObjectNode parent, Map<String, Schema> schemas) {
        writeSchemas(parent, schemas, ComponentsConstant.PROP_SCHEMAS);
    }

    /**
     * Writes a map of {@link Schema} to the JSON tree.
     *
     * @param parent
     * @param schemas
     */
    private static void writeSchemas(ObjectNode parent, Map<String, Schema> schemas, String propertyName) {
        if (schemas == null) {
            return;
        }
        ObjectNode schemasNode = parent.putObject(propertyName);
        for (Map.Entry<String, Schema> entry : schemas.entrySet()) {
            writeSchema(schemasNode, entry.getValue(), entry.getKey());
        }
    }

    /**
     * Writes a {@link Schema} to the JSON tree.
     *
     * @param parent the parent json node
     * @param model Schema model
     * @param name name of the node
     */
    public static void writeSchema(ObjectNode parent, Schema model, String name) {
        if (model == null) {
            return;
        }
        parent.set(name, createSchemaNode(model, parent));
    }

    private static JsonNode createRefSchemaNode(Schema model, JsonNodeCreator factory) {
        ObjectNode result = factory.objectNode();
        String ref = model.getRef();
        if (ref != null) {
            result.put(Referenceable.PROP_$REF, ref);
        }
        String title = model.getTitle();
        if (title != null) {
            result.put(PROP_TITLE, title);
        }
        String description = model.getDescription();
        if (description != null) {
            result.put(PROP_DESCRIPTION, description);
        }
        return result;
    }

    public static JsonNode createSchemaNode(Schema model, JsonNodeCreator factory) {
        Boolean booleanValue = model.getBooleanSchema();
        if (booleanValue != null) {
            return factory.booleanNode(booleanValue);
        } else {
            SchemaImpl impl = (SchemaImpl) model;
            Map<String, Object> data = impl.getDataMap();
            return getMapNode(data, factory);
        }
    }

    private static JsonNode getMapNode(Map<String, ?> map, JsonNodeCreator factory) {
        ObjectNode result = factory.objectNode();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            result.set(entry.getKey(), getObjectNode(entry.getValue(), factory));
        }
        return result;
    }

    private static JsonNode getListNode(List<?> list, JsonNodeCreator factory) {
        ArrayNode result = factory.arrayNode();
        for (Object entry : list) {
            result.add(getObjectNode(entry, factory));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static JsonNode getObjectNode(Object value, JsonNodeCreator factory) {
        if (value == null) {
            return factory.nullNode();
        } else if (value instanceof Schema) {
            return createSchemaNode((Schema) value, factory);
        } else if (value instanceof XML) {
            return XmlWriter.createXMLNode(factory, (XML) value);
        } else if (value instanceof ExternalDocumentation) {
            return ExternalDocsWriter.createExternalDocumentationNode(factory, (ExternalDocumentation) value);
        } else if (value instanceof Discriminator) {
            return DiscriminatorWriter.createDiscriminatorNode(factory, (Discriminator) value);
        } else if (value instanceof List<?>) {
            return getListNode((List<?>) value, factory);
        } else if (value instanceof Map<?, ?>) {
            return getMapNode((Map<String, ?>) value, factory);
        } else if (value instanceof Enum<?>) {
            return factory.textNode(value.toString().toLowerCase());
        } else {
            return ObjectWriter.convertObjectToNode(factory, value);
        }
    }

}
