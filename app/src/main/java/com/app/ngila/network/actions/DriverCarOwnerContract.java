package com.app.ngila.network.actions;

public class DriverCarOwnerContract extends NetworkAction {
    private String action ="DriverCarOwnerContract";
    private String time;
    private String carOwnerNumber;
    private String driverNumber;
    private String driverLocation;
    private String driverActivity;
    public DriverCarOwnerContract(){}
    public DriverCarOwnerContract(String time,String carOwnerNumber,
                                  String driverNumber,String driverLocation,String driverActivity){
        this.time = time;
        this.carOwnerNumber = carOwnerNumber;
        this.driverNumber = driverNumber;
        this.driverLocation = driverLocation;
        this.driverActivity = driverActivity;
    }
    @Override
    public String getAction() {
        return action;
    }

    @Override
    public void setAction(String action) {

    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCarOwnerNumber() {
        return carOwnerNumber;
    }

    public void setCarOwnerNumber(String carOwnerNumber) {
        this.carOwnerNumber = carOwnerNumber;
    }

    public String getDriverNumber() {
        return driverNumber;
    }

    public void setDriverNumber(String driverNumber) {
        this.driverNumber = driverNumber;
    }

    public String getDriverLocation() {
        return driverLocation;
    }

    public void setDriverLocation(String driverLocation) {
        this.driverLocation = driverLocation;
    }

    public String getDriverActivity() {
        return driverActivity;
    }

    public void setDriverActivity(String driverActivity) {
        this.driverActivity = driverActivity;
    }
}
