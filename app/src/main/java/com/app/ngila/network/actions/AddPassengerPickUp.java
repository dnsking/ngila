package com.app.ngila.network.actions;

public class AddPassengerPickUp extends NetworkAction{
    private String action = "AddPassengerPickUp";
    private String city;
    private String phoneNumber;
    private String passengerLocation;
    private String orderTime ;
    private String destinationName ;
    private String destination;
    public AddPassengerPickUp(){}
    public AddPassengerPickUp(String city,String phoneNumber,String passengerLocation,String orderTime, String destinationName
            , String destination){
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.passengerLocation = passengerLocation;
        this.orderTime = orderTime;
        this.destinationName = destinationName;
        this.destination = destination;
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

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPassengerLocation() {
        return passengerLocation;
    }

    public void setPassengerLocation(String passengerLocation) {
        this.passengerLocation = passengerLocation;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }
}
