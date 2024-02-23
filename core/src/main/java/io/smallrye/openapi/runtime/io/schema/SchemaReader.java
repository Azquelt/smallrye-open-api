package io.smallrye.openapi.runtime.io.schema;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.microprofile.openapi.models.media.Schema;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.smallrye.openapi.api.models.media.SchemaImpl;
import io.smallrye.openapi.runtime.io.IoLogging;
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
            return SchemaImpl.getOrCreateFromNode((ObjectNode) node);
        } else if (node.isBoolean()) {
            return SchemaImpl.ofBoolean(node.booleanValue());
        } else {
            return null;
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
}
