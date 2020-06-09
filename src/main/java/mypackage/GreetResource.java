
package mypackage;

import java.util.Collections;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.SseEventSink;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * A simple JAX-RS resource to greet you. Examples:
 * <p>
 * Get default greeting message:
 * curl -X GET http://localhost:8080/greet
 * <p>
 * The message is returned as a JSON object.
 */
@Path("/example")
public class GreetResource {
    private static final JsonBuilderFactory JSON = Json.createBuilderFactory(Collections.emptyMap());

    private final String message;
    private final MsgProcessingBean msgBean;

    @Inject
    public GreetResource(MsgProcessingBean msgBean, @ConfigProperty(name = "app.greeting") String message) {
        this.message = message;
        this.msgBean = msgBean;
    }

    /**
     * Process send.
     *
     * @param msg message to process
     */
    @Path("/send/{msg}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void getSend(@PathParam("msg") String msg) {
        msgBean.process(msg);
    }

    /**
     * Consume event.
     *
     * @param eventSink sink
     * @param sse       event
     */
    @GET
    @Path("sse")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void listenToEvents(@Context SseEventSink eventSink) {
        msgBean.addSink(eventSink);
    }

    /**
     * Return a wordly greeting message.
     *
     * @return {@link JsonObject}
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getDefaultMessage() {
        String msg = String.format("%s %s!", message, "World");
        return JSON.createObjectBuilder()
                .add("message", msg)
                .build();
    }
}
