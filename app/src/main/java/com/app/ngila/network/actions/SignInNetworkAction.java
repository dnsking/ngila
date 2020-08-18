package com.app.ngila.network.actions;

public class SignInNetworkAction extends NetworkAction {
    private String action="SignIn";
    private String phoneNumber;

    public SignInNetworkAction(){}
    public SignInNetworkAction(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }
    public SignInNetworkAction(String phoneNumber,String action){
        this.phoneNumber = phoneNumber;
        this.action = action;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getAction() {
        return action;
    }

    @Override
    public void setAction(String action) {

    }
}
