package com.nguyenthaibaoduy.lab306.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nguyenthaibaoduy.lab306.model.DeviceTelemetry;
import com.nguyenthaibaoduy.lab306.repository.TelemetryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class MqttService {

    @Autowired
    private TelemetryRepository repository;

    private ObjectMapper mapper = new ObjectMapper();

    // Hàm này tự chạy khi có tin nhắn từ ESP gửi về topic: smarthome/+/telemetry
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<String> message) {
        try {
            String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
            String payload = message.getPayload(); 
            
            // In ra log để debug
            System.out.println("📩 MQTT [" + topic + "]: " + payload);

            // 1. Lấy deviceId từ topic. VD: smarthome/device_6/telemetry
            String[] parts = topic.split("/");
            if (parts.length < 2) return; // Bỏ qua nếu topic sai định dạng
            String deviceId = parts[1]; // lấy "device_6"

            // 2. Đọc JSON từ ESP32
            // Payload mẫu: {"power": 120.5, "current": 0.55, "voltage": 220.1, "energy": 1.2, "state": "ON"}
            JsonNode json = mapper.readTree(payload);
            
            DeviceTelemetry data = new DeviceTelemetry();
            data.setDeviceId(deviceId);
            
            // Dùng .has() để tránh lỗi null nếu ESP gửi thiếu trường
            if (json.has("power")) data.setPower(json.get("power").asDouble());
            if (json.has("current")) data.setCurrent(json.get("current").asDouble());
            if (json.has("voltage")) data.setVoltage(json.get("voltage").asDouble());
            if (json.has("energy")) data.setEnergy(json.get("energy").asDouble());
            if (json.has("state")) data.setState(json.get("state").asText());

            // 3. Lưu vào Database
            repository.save(data);
            System.out.println("✅ Da luu data thiet bi: " + deviceId);

        } catch (Exception e) {
            System.err.println("❌ Loi xu ly  MQTT: " + e.getMessage());
            // e.printStackTrace(); // Bật lên nếu cần debug kỹ
        }
    }
}