package com.app.ngila.network.actions;

public class DriverWaiting  extends NetworkAction{
    private String action ="addDriverWaiting";
    private String city;
    private String phoneNumber;
    public DriverWaiting(){}
    public DriverWaiting( String city,String phoneNumber){
        this.city = city;
        this.phoneNumber = phoneNumber;
    }
    @Override
    public String getAction() {
        return action;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
