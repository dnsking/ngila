package com.app.ngila.network.actions;

public class AddPassengerPickUp extends NetworkAction{
    private String action = "AddPassengerPickUp";
    private String city;
    private String location;
    private String destination;
    private String phoneNumber;
    public AddPassengerPickUp(){}
    public AddPassengerPickUp(String city, String location,String destination,String phoneNumber){
        this.city = city;
        this.location = location;
        this.destination = destination;
        this.phoneNumber = phoneNumber;
    }
    @Override
    public String getAction() {
        return action;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void setAction(String action) {

    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
