package com.flink.vehiclestate.datatypes;

public class DeviceEventMessage {
    private String vehicleId;
    private String paragraph;
    private String timeStamp;
    private String heartbeatLatitude;
    private String heartbeatLongitude;

    public String getVehicleId() {
        return vehicleId;
    }

    public DeviceEventMessage setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
        return this;
    }

    public String getParagraph() {
        return paragraph;
    }

    public DeviceEventMessage setParagraph(String paragraph) {
        this.paragraph = paragraph;
        return this;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public DeviceEventMessage setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public String getHeartbeatLatitude() {
        return heartbeatLatitude;
    }

    public DeviceEventMessage setHeartbeatLatitude(String heartbeatLatitude) {
        this.heartbeatLatitude = heartbeatLatitude;
        return this;
    }

    public String getHeartbeatLongitude() {
        return heartbeatLongitude;
    }

    public DeviceEventMessage setHeartbeatLongitude(String heartbeatLongitude) {
        this.heartbeatLongitude = heartbeatLongitude;
        return this;
    }

    @Override
    public String toString() {
        return "DeviceEventMessage{" +
                "vehicleId='" + vehicleId + '\'' +
                ", paragraph='" + paragraph + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", heartbeatLatitude='" + heartbeatLatitude + '\'' +
                ", heartbeatLongitude='" + heartbeatLongitude + '\'' +
                '}';
    }
}
