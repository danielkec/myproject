package mypackage;

public class Car {
    private Integer doors;
    private String vin;

    public Car() {
    }

    public Car(final Integer doors, final String vin) {
        this.doors = doors;
        this.vin = vin;
    }

    public Integer getDoors() {
        return doors;
    }

    public void setDoors(final Integer doors) {
        this.doors = doors;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(final String vin) {
        this.vin = vin;
    }
}
