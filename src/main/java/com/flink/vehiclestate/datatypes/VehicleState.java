package com.flink.vehiclestate.datatypes;

import java.io.Serializable;

public class VehicleState implements Serializable {

    private String timestamp;

    private String vehicleId;

    private Long duration;

    private Double mileage;

    private String heartbeatLatitude;
    private String heartbeatLongitude;


    public String getTimestamp() {
        return timestamp;
    }

    public VehicleState setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }


    public String getVehicleId() {
        return vehicleId;
    }

    public VehicleState setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
        return this;
    }

    public Long getDuration() {
        return duration;
    }

    public VehicleState setDuration(Long duration) {
        this.duration = duration;
        return this;
    }

    public Double getMileage() {
        return mileage;
    }

    public VehicleState setMileage(Double mileage) {
        this.mileage = mileage;
        return this;
    }

    public String getHeartbeatLatitude() {
        return heartbeatLatitude;
    }

    public VehicleState setHeartbeatLatitude(String heartbeatLatitude) {
        this.heartbeatLatitude = heartbeatLatitude;
        return this;
    }

    public String getHeartbeatLongitude() {
        return heartbeatLongitude;
    }

    public VehicleState setHeartbeatLongitude(String heartbeatLongitude) {
        this.heartbeatLongitude = heartbeatLongitude;
        return this;
    }

    @Override
    public String toString() {
        return "VehicleState{" +
                "timestamp='" + timestamp + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", duration=" + duration +
                ", mileage=" + mileage +
                ", heartbeatLatitude='" + heartbeatLatitude + '\'' +
                ", heartbeatLongitude='" + heartbeatLongitude + '\'' +
                '}';
    }
}
