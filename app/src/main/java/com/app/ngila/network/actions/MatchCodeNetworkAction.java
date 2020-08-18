package com.app.ngila.network.actions;

import com.app.ngila.data.NgilaUser;

public class MatchCodeNetworkAction extends NetworkAction {
    protected String action = "MatchCode";
    private String phoneNumber;
    private String code;
    private NgilaUser data;
    public MatchCodeNetworkAction(){}
    public MatchCodeNetworkAction(String phoneNumber,  String code
    ,NgilaUser data){
        this.phoneNumber = phoneNumber;
        this.code = code;
        this.data = data;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public NgilaUser getData() {
        return data;
    }

    public void setData(NgilaUser data) {
        this.data = data;
    }
}
