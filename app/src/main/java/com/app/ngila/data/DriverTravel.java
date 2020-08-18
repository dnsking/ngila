package com.app.ngila.data;

public class DriverTravel {
    private String driverNumber;//primary key
    private String carOwnerNumber;
    private String location;
    private String passenger;
    private String what;
    private String rideIdAndLocation;
    private String rideId;
    private String time;

    public String getDriverNumber() {
        return driverNumber;
    }

    public void setDriverNumber(String driverNumber) {
        this.driverNumber = driverNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public String getCarOwnerNumber() {
        return carOwnerNumber;
    }

    public void setCarOwnerNumber(String carOwnerNumber) {
        this.carOwnerNumber = carOwnerNumber;
    }

    public String getPassenger() {
        return passenger;
    }

    public void setPassenger(String passenger) {
        this.passenger = passenger;
    }

    public String getRideIdAndLocation() {
        return rideIdAndLocation;
    }

    public void setRideIdAndLocation(String rideIdAndLocation) {
        this.rideIdAndLocation = rideIdAndLocation;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
