package mypackage;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@RegisterRestClient(baseUri="http://localhost:7001/example")
@Path("/cars")
@Produces("application/json")
@Consumes("application/json")
public interface CarClient {

    @OPTIONS
    Response options();

    @HEAD
    Response head();

    @GET
    List<Car> getCars();

    @GET
    @Path("/{vin}")
    Car getCar(@PathParam("vin") String vin);
}
