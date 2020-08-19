package com.app.ngila.network.actions;

import com.app.ngila.data.AvailableCars;

public class AvailableCarsNetworkAction extends NetworkAction {
    private String action ="AddAvailableCars";
    private AvailableCars data;
    @Override
    public String getAction() {
        return action;
    }

    @Override
    public void setAction(String action) {

    }

    public AvailableCars getData() {
        return data;
    }

    public void setData(AvailableCars data) {
        this.data = data;
    }
}
