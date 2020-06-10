
package mypackage;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
import javax.ws.rs.sse.Sse;
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

    private CarsBean carsBean;
    private final String message;
    private final MsgProcessingBean msgBean;
    private final static List<Car> CARS = List.of(
            new Car(5, "7AM 4200"),
            new Car(3, "7AM 4201"),
            new Car(7, "7AM 4202"),
            new Car(5, "7AM 4203"),
            new Car(3, "7AM 4204")
    );

    @Inject
    public GreetResource(MsgProcessingBean msgBean, CarsBean carsBean) {
        this.carsBean = carsBean;
        this.message = "test";
        this.msgBean = msgBean;
    }

    @Path("/send/{msg}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void getSend(@PathParam("msg") String msg) {
        msgBean.process(msg);
    }

    @GET
    @Path("sse")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void listenToEvents(@Context SseEventSink eventSink) {
        msgBean.addSink(eventSink);
    }

    @GET
    @Path("sse-broadcaster")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void registerBroadcaster(@Context SseEventSink eventSink, @Context Sse sse) {
        Objects.requireNonNull(sse);
        Objects.requireNonNull(eventSink);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("cars")
    public List<Car> getCars() {
        return CARS;
    }

    @Path("/cars/{vin}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Car getCar(@PathParam("vin") String vin) {
        return CARS.stream().filter(car -> car.getVin().contains(vin)).findFirst().get();
    }

    @Path("/client/{vin}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Car getClientCall(@PathParam("vin") String vin) {
        return carsBean.callClient(vin);
    }
}
