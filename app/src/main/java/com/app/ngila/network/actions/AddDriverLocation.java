package com.app.ngila.network.actions;

public class AddDriverLocation extends NetworkAction{
    private String action ="AddDriverLocation";
    private String phoneNumber;
    private String location;

    public AddDriverLocation(){}
    public AddDriverLocation(String phoneNumber,String location){
        this.phoneNumber = phoneNumber;
        this.location = location;
    }

    @Override
    public String getAction() {
        return action;
    }

    @Override
    public void setAction(String action) {

    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
