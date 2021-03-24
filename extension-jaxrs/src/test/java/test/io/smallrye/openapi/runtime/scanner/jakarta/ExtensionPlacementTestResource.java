package test.io.smallrye.openapi.runtime.scanner.jakarta;

import org.eclipse.microprofile.openapi.annotations.extensions.Extension;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path(value = "/ext")
public class ExtensionPlacementTestResource {

    @Schema
    @Extension(name = "model-schema-ext", value = "{ \"key\":\"value\" }", parseValue = true)
    public static class Model {

        @Schema
        @Extension(name = "value1-ext", value = "plain string", parseValue = true)
        String value1;
        @Schema
        @Extension(name = "value2-ext", value = "plain string", parseValue = false)
        Integer value2;
    }

    @GET
    @Path(value = "segment1")
    @Consumes(value = MediaType.TEXT_PLAIN)
    @Produces(value = MediaType.TEXT_PLAIN)
    @Extension(name = "operation-ext", value = "plain string")
    public Model get(
            @QueryParam(value = "data") @Parameter @Extension(name = "qparam-data-ext", value = "1", parseValue = true) String data) {
        return null;
    }

}
