package com.flink.vehiclestate.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flink.vehiclestate.datatypes.DeviceEventMessage;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

/**
 * reprocess Kafka input stream to instanced object stream
 */
public class EventFormat implements FlatMapFunction<String, DeviceEventMessage> {
    @Override
    public void flatMap(String jsonStr, Collector<DeviceEventMessage> out) throws Exception {
        try {
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            DeviceEventMessage deviceEventMessage = new DeviceEventMessage()
                    .setVehicleId(jsonObject.getString("vehicle_id"))
                    .setParagraph(jsonObject.getString("paragraph"))
                    .setTimeStamp(jsonObject.getString("time_stamp"))
                    .setHeartbeatLatitude(jsonObject.getString("heartbeat_gps_latitude"))
                    .setHeartbeatLongitude(jsonObject.getString("heartbeat_gps_longitude"));
            out.collect(deviceEventMessage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
