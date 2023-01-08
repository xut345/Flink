package com.flink.vehiclestate;

import java.time.Duration;
import java.time.ZoneOffset;

import com.flink.vehiclestate.consumer.KafkaConsumerBuilder;
import com.flink.vehiclestate.datatypes.DeviceEventMessage;
import com.flink.vehiclestate.map.EventFormat;
import com.flink.vehiclestate.process.VehicleMileageAndDurationAccumulation;
import com.flink.vehiclestate.utils.ISO8601DateTime;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class VehicleStateJob {

  public static void main(String[] args) throws Exception {
    ParameterTool parameterTool = ParameterTool.fromArgs(args);

    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.enableCheckpointing(30 * 1000, CheckpointingMode.EXACTLY_ONCE);
    int sourceParallelism = 2;
    env.fromSource(KafkaConsumerBuilder.buildConsumer(parameterTool), WatermarkStrategy.noWatermarks(),
            "Kafka Source")
        .setParallelism(sourceParallelism).uid("source-id").name("data-stream-source")
            .flatMap(new EventFormat())
        .assignTimestampsAndWatermarks(
            WatermarkStrategy.<DeviceEventMessage>forBoundedOutOfOrderness(
                    Duration.ofSeconds(120))
                .withTimestampAssigner(
                    (event, timestamp) -> ISO8601DateTime.parse(pevent.getTimeStamp()).toInstant(
                        ZoneOffset.UTC).toEpochMilli()))
        .keyBy((KeySelector<DeviceEventMessage, String>) deviceEventMessage ->
                deviceEventMessage.getVehicleId() + "_" + deviceEventMessage.getParagraph())
        .process(new VehicleMileageAndDurationAccumulation()).name("driving-time-accumulation")
            .print();

    String jobName = "Vehicle gps realtime update Job";
    JobExecutionResult result = env.execute(jobName);
    System.out.printf("Flink Job, jobName[%s], jobId[$s].%n", jobName, result.getJobID());
  }
}
