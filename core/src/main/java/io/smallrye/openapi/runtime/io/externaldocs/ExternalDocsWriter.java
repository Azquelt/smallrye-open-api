package io.smallrye.openapi.runtime.io.externaldocs;

import org.eclipse.microprofile.openapi.models.ExternalDocumentation;

import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.smallrye.openapi.runtime.io.JsonUtil;
import io.smallrye.openapi.runtime.io.extension.ExtensionWriter;

/**
 * This writes External Documentation json
 *
 * @see <a href=
 *      "https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#externalDocumentationObject">externalDocumentationObject</a>
 *
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 * @author Eric Wittmann (eric.wittmann@gmail.com)
 */
public class ExternalDocsWriter {

    private ExternalDocsWriter() {
    }

    /**
     * Writes the {@link ExternalDocumentation} model to the JSON tree.
     *
     * @param parent the parent json model
     * @param model the ExternalDocumentation model
     */
    public static void writeExternalDocumentation(ObjectNode parent, ExternalDocumentation model) {
        if (model == null) {
            return;
        }
        
        parent.set(ExternalDocsConstant.PROP_EXTERNAL_DOCS, createExternalDocumentationNode(parent, model));
    }
    
    public static ObjectNode createExternalDocumentationNode(JsonNodeCreator creator, ExternalDocumentation model) {
        ObjectNode node = creator.objectNode();
        
        JsonUtil.stringProperty(node, ExternalDocsConstant.PROP_DESCRIPTION, model.getDescription());
        JsonUtil.stringProperty(node, ExternalDocsConstant.PROP_URL, model.getUrl());
        ExtensionWriter.writeExtensions(node, model);
        
        return node;
    }
}
