package com.nguyenthaibaoduy.lab306.controller;

import com.nguyenthaibaoduy.lab306.model.Device;
import com.nguyenthaibaoduy.lab306.model.DeviceLog;
import com.nguyenthaibaoduy.lab306.mqtt.MqttGateway;
import com.nguyenthaibaoduy.lab306.repository.DeviceRepository;
import com.nguyenthaibaoduy.lab306.repository.DeviceLogRepository; // Thêm Repository mới
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/mobile")
@CrossOrigin(origins = "*") 
public class MobileDeviceController {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceLogRepository deviceLogRepository; // Inject Repository mới

    @Autowired
    private MqttGateway mqttGateway;

    @PostMapping("/add")
    public Device addDevice(@RequestBody Device device) {
        Device savedDevice = deviceRepository.save(device);
        String message = "Device: " + savedDevice.getName() + " | Room: " + savedDevice.getRoom();
        mqttGateway.sendToMqtt(message, "devices/notifications"); //
        return savedDevice;
    }

    @GetMapping("/room/{roomName}")
    public List<Device> getDevicesByRoom(@PathVariable String roomName) {
        if ("All Rooms".equalsIgnoreCase(roomName) || "All".equalsIgnoreCase(roomName)) {
            return deviceRepository.findAll();
        }
        return deviceRepository.findByRoom(roomName);
    }

    @PostMapping("/control")
    public String controlDevice(@RequestBody Map<String, Object> payload) {
        try {
            Long dbId = Long.valueOf(payload.get("id").toString());
            String topicSuffix = (String) payload.get("topic");
            String action = (String) payload.get("action");

            // A. Gửi lệnh MQTT
            String mqttTopic = "smarthome/" + topicSuffix + "/command";
            mqttGateway.sendToMqtt(action, mqttTopic);

            // B. Cập nhật DB & Lưu Log lịch sử
            Optional<Device> deviceOpt = deviceRepository.findById(dbId);
            if (deviceOpt.isPresent()) {
                Device device = deviceOpt.get();
                device.setIsOn("ON".equalsIgnoreCase(action)); //
                deviceRepository.save(device);

                // 👇 LƯU LỊCH SỬ VÀO DATABASE
                DeviceLog log = new DeviceLog(device.getId(), device.getName(), action, device.getRoom());
                deviceLogRepository.save(log);

                // 👇 BẮN TIN NHẮN LỊCH SỬ LÊN MQTTX ĐỂ XEM
                String logMsg = "History: " + device.getName() + " turned " + action + " in " + device.getRoom();
                mqttGateway.sendToMqtt(logMsg, "device/logs/all");
            }
            return "Success";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // API MỚI: Lấy lịch sử để hiển thị trên App Flutter
    @GetMapping("/logs/{deviceId}")
    public List<DeviceLog> getDeviceLogs(@PathVariable Long deviceId) {
        return deviceLogRepository.findByDeviceIdOrderByTimestampDesc(deviceId);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteDevice(@PathVariable Long id) {
        if (deviceRepository.existsById(id)) {
            Device d = deviceRepository.findById(id).get();
            mqttGateway.sendToMqtt("Device deleted: " + d.getName(), "admin/logs");
            deviceRepository.deleteById(id);
            return "Deleted";
        }
        return "Not Found";
    }
}