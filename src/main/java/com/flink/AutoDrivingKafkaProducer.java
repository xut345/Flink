package com.flink;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Properties;
import java.util.TimeZone;

import com.alibaba.fastjson2.JSONObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import javax.swing.plaf.IconUIResource;

public class AutoDrivingKafkaProducer {

  private static final String BROKER_LIST = "node01:9092,node02:9092,node03:9092";
  private static final SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  private static final String TOPIC = "vehicle_heartbeat";

  static {
    dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
  }

  private static KafkaProducer<String, String> producer;

  public static void main(String[] args) throws FileNotFoundException {
    initKafkaProducer();

    try (BufferedReader br = Files.newBufferedReader(Paths.get("vehicle_gps.csv"))) {
      String DELIMITER = ",";
      String line;
      while ((line = br.readLine()) != null) {
        if (line.startsWith("vehicle_id")) {
          continue;
        }
        String[] columns = line.split(DELIMITER);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("vehicle_id",columns[0]);
        jsonObject.put("paragraph",columns[1]);
        jsonObject.put("time_stamp",columns[2]);
        jsonObject.put("heartbeat_gps_latitude",columns[3]);
        jsonObject.put("heartbeat_gps_longitude",columns[4]);
        producer.send(new ProducerRecord<>(TOPIC, jsonObject.getString("vehicle_id"), jsonObject.toJSONString()));
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }

  }

  public static KafkaProducer<String, String> initKafkaProducer() {
    if (Objects.isNull(producer)) {
      Properties props = new Properties();
      props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_LIST);
      props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
      props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
      props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
      props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 10000);
      props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
      producer = new KafkaProducer<>(props);
    }
    return producer;
  }
}