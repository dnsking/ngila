package com.app.ngila.network.actions;

public class AcceptedDriver extends NetworkAction {
    private String action ="AcceptedDriver";
    private String driverNumber;
    private String carOwnerNumber;
    private String name;
    private String time;
    private String driverLocation;
    private String carOwnerLocation;
    public AcceptedDriver(){}
    public AcceptedDriver(String driverNumber,String carOwnerNumber,String name,String time,String driverLocation,String carOwnerLocation){
        this.driverNumber = driverNumber;
        this.carOwnerNumber = carOwnerNumber;
        this.name = name;
        this.time = time;
        this.driverLocation = driverLocation;
        this.carOwnerLocation = carOwnerLocation;
    }
    @Override
    public String getAction() {
        return action;
    }

    @Override
    public void setAction(String action) {

    }

    public String getDriverNumber() {
        return driverNumber;
    }

    public void setDriverNumber(String driverNumber) {
        this.driverNumber = driverNumber;
    }

    public String getCarOwnerNumber() {
        return carOwnerNumber;
    }

    public void setCarOwnerNumber(String carOwnerNumber) {
        this.carOwnerNumber = carOwnerNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDriverLocation() {
        return driverLocation;
    }

    public void setDriverLocation(String driverLocation) {
        this.driverLocation = driverLocation;
    }

    public String getCarOwnerLocation() {
        return carOwnerLocation;
    }

    public void setCarOwnerLocation(String carOwnerLocation) {
        this.carOwnerLocation = carOwnerLocation;
    }
}
