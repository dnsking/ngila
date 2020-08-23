package com.app.ngila.data;

public class DriverOrder {
    private String driverNumber;//primary key
    private String passengerNumber;
    private String passengerLocation;
    private String driverLocation;

    private String carOwnerNumber;
    private String destination;
    private String destinationName;
    private String orderTime;//sort key
    private String arriveTime;
    private String distance;
    private String charge;
    private String rideId;


    public DriverOrder(){}
    public String getDriverNumber() {
        return driverNumber;
    }

    public void setDriverNumber(String driverNumber) {
        this.driverNumber = driverNumber;
    }

    public String getPassengerNumber() {
        return passengerNumber;
    }

    public void setPassengerNumber(String passengerNumber) {
        this.passengerNumber = passengerNumber;
    }

    public String getCarOwnerNumber() {
        return carOwnerNumber;
    }

    public void setCarOwnerNumber(String carOwnerNumber) {
        this.carOwnerNumber = carOwnerNumber;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(String arriveTime) {
        this.arriveTime = arriveTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getPassengerLocation() {
        return passengerLocation;
    }

    public void setPassengerLocation(String passengerLocation) {
        this.passengerLocation = passengerLocation;
    }

    public String getDriverLocation() {
        return driverLocation;
    }

    public void setDriverLocation(String driverLocation) {
        this.driverLocation = driverLocation;
    }
}
