package mypackage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class CarsBean {

    @Inject
    @RestClient
    private CarClient client;

    public Car callClient(String vin) {
        return client.getCar(vin);
    }
}
