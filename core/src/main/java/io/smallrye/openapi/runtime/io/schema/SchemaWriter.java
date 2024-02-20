package io.smallrye.openapi.runtime.io.schema;

import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.openapi.models.media.Schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.smallrye.openapi.api.models.media.SchemaImpl;
import io.smallrye.openapi.runtime.io.components.ComponentsConstant;

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
        parent.set(name, getSchemaNode(model));
    }
    
    private static JsonNode getSchemaNode(Schema model) {
        if (model instanceof SchemaImpl) {
            SchemaImpl impl = (SchemaImpl) model;
            return impl.getJsonNode();
        }
        if (model == null) {
            return JsonNodeFactory.instance.nullNode();
        }
        throw new UnsupportedOperationException("Cannot write a different schema impl");
    }

}
