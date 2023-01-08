package com.flink.vehiclestate.process;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.flink.vehiclestate.datatypes.DeviceEventMessage;
import com.flink.vehiclestate.datatypes.VehicleState;
import com.flink.vehiclestate.utils.GpsDistance;
import com.flink.vehiclestate.utils.ISO8601DateTime;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

public class VehicleMileageAndDurationAccumulation extends
    KeyedProcessFunction<String, DeviceEventMessage, VehicleState> {

  private transient ValueState<VehicleState> deviceState;

  @Override
  public void open(Configuration parameters) {
    ValueStateDescriptor<VehicleState> stateDescriptor =
        new ValueStateDescriptor<>("device-state", VehicleState.class);
    deviceState = getRuntimeContext().getState(stateDescriptor);
  }

  @Override
  public void processElement(
      DeviceEventMessage deviceEventMessage,
      Context context,
      Collector<VehicleState> collector) throws Exception {
    VehicleState previousVehicleState = deviceState.value();
    if (previousVehicleState == null) {
      VehicleState newRecord = new VehicleState()
          .setVehicleId(deviceEventMessage.getVehicleId())
          .setTimestamp(deviceEventMessage.getTimeStamp())
          .setHeartbeatLatitude(deviceEventMessage.getHeartbeatLatitude())
          .setHeartbeatLongitude(deviceEventMessage.getHeartbeatLongitude())
          .setDuration(0L)
          .setMileage(0D);
      deviceState.update(newRecord);
      collector.collect(newRecord);

      ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("+8"));
      ZonedDateTime zonedDateTimeBeijing = zonedDateTime.withHour(23).withMinute(59)
          .withSecond(59).withNano(0);
      ZonedDateTime zonedDateTimeUTC = zonedDateTimeBeijing.withZoneSameInstant(ZoneId.of("UTC"));

      long endOfDay = zonedDateTimeUTC.toEpochSecond() * 1000;
      context.timerService().registerProcessingTimeTimer(endOfDay);
    } else {
      Long previousTimestamp =
          ISO8601DateTime.parse(previousVehicleState.getTimestamp()).toEpochSecond(ZoneOffset.UTC)
              * 1000;
      Long nowTimestamp =
          ISO8601DateTime.parse(deviceEventMessage.getTimeStamp()).toEpochSecond(ZoneOffset.UTC)
              * 1000;
      long duration = (nowTimestamp - previousTimestamp) / 1000;  //second
      if (duration > 0) {
        previousVehicleState.setTimestamp(deviceEventMessage.getTimeStamp());
        if (duration < 20 * 60) {
          double distance = GpsDistance.calculateDistance(
                  Double.parseDouble(previousVehicleState.getHeartbeatLatitude()),
                  Double.parseDouble(previousVehicleState.getHeartbeatLongitude()),
                  Double.parseDouble(deviceEventMessage.getHeartbeatLatitude()),
                  Double.parseDouble(deviceEventMessage.getHeartbeatLongitude())
          );  //km
          //If speed exceeds 120 regard as bad data, calculate with 60km/h
          if (distance / (duration / 3600.0) > 120) {
            distance = (duration / 3600.0) * 60;
          }
          previousVehicleState.setDuration(previousVehicleState.getDuration() + duration);
          previousVehicleState.setMileage(previousVehicleState.getMileage() + distance);
        }
        deviceState.update(previousVehicleState);
        collector.collect(previousVehicleState);
      }
    }
  }

  @Override
  public void onTimer(
      long timestamp,
      OnTimerContext ctx,
      Collector<VehicleState> out) throws Exception {
    VehicleState previousVehicleState = deviceState.value();
    if (previousVehicleState != null) {
      previousVehicleState.setDuration(0L);
      previousVehicleState.setMileage(0D);
      out.collect(previousVehicleState);
      deviceState.clear();
    }
  }
}
