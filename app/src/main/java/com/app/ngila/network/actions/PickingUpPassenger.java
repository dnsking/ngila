package com.app.ngila.network.actions;

import com.app.ngila.data.AvailableCars;

public class PickingUpPassenger extends NetworkAction {
    private String action ="PickingUpPassenger";
    private String location;
    private String locationName;
    private String passengerNumber;
    @Override
    public String getAction() {
        return action;
    }

    @Override
    public void setAction(String action) {

    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getPassengerNumber() {
        return passengerNumber;
    }

    public void setPassengerNumber(String passengerNumber) {
        this.passengerNumber = passengerNumber;
    }
}
