package com.app.ngila.data;

public class DriverTravel {
    private String driverNumber;//primary key
    private String carOwnerNumber;
    private String location;
    private String what;

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
}
