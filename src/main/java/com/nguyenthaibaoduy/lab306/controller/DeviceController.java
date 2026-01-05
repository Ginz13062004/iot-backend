package com.nguyenthaibaoduy.lab306.controller;

import com.nguyenthaibaoduy.lab306.model.Device;
import com.nguyenthaibaoduy.lab306.model.DeviceLog;
import com.nguyenthaibaoduy.lab306.model.DeviceTelemetry;
import com.nguyenthaibaoduy.lab306.mqtt.MqttGateway;
import com.nguyenthaibaoduy.lab306.repository.DeviceRepository;
import com.nguyenthaibaoduy.lab306.repository.DeviceLogRepository;
import com.nguyenthaibaoduy.lab306.repository.TelemetryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.text.Normalizer;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/devices") // Cả Web và App đều gọi vào đây
@CrossOrigin(origins = "*") 
public class DeviceController {

    @Autowired
    private MqttGateway mqttGateway;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceLogRepository deviceLogRepository;

    @Autowired
    private TelemetryRepository telemetryRepo;

    // --- API THÊM THIẾT BỊ ---
    @PostMapping("/add")
    public Device addDevice(@RequestBody Device device) {
        String rawRoom = device.getRoom() != null ? device.getRoom() : "default";
        String rawName = device.getName() != null ? device.getName() : "device";

        String formattedTopic = removeAccent(rawRoom).toLowerCase().replaceAll("\\s+", "") 
                              + "/" 
                              + removeAccent(rawName).toLowerCase().replaceAll("\\s+", "");

        device.setTopic(formattedTopic);
        device.setIsOn(false);
        
        Device savedDevice = deviceRepository.save(device);

        // Bắn thông báo lên MQTTX cho cả Web và App thấy
        mqttGateway.sendToMqtt("SYSTEM: New device added: " + savedDevice.getName(), "devices/notifications");

        return savedDevice;
    }

    // --- API ĐIỀU KHIỂN CHUNG (FIX CHO CẢ WEB & MOBILE) ---
   @PostMapping("/control")
    public String controlDevice(@RequestBody Map<String, Object> payload) {
        try {
            String action = (String) payload.get("action");
            String deviceIdStr = (String) payload.get("deviceId"); // Ví dụ: "device_4" hoặc "device_6"
            
            // 1. Tự động lấy số ID từ chuỗi (Ví dụ "device_4" -> lấy được số 4)
            Long id = null;
            if (deviceIdStr != null && deviceIdStr.contains("_")) {
                try {
                    id = Long.parseLong(deviceIdStr.split("_")[1]);
                } catch (Exception e) {
                    System.out.println("❌ Khong parse đuoc ID tu: " + deviceIdStr);
                }
            }

            // 2. Tìm trong Database để lấy Topic chuẩn (Ví dụ: livingroom/quat)
            Optional<Device> deviceOpt = (id != null) ? deviceRepository.findById(id) : Optional.empty();
            
            String targetTopic;
            if (deviceOpt.isPresent()) {
                targetTopic = deviceOpt.get().getTopic(); // Lấy topic thật từ MySQL
            } else {
                targetTopic = "smarthome/" + deviceIdStr; // Topic dự phòng
            }

            // 3. Bắn MQTT vào đúng địa chỉ
            String fullMqttTopic = targetTopic + "/command";
            mqttGateway.sendToMqtt(action, fullMqttTopic);

            // 4. Cập nhật trạng thái và Lưu lịch sử (Logs)
            if (deviceOpt.isPresent()) {
                Device d = deviceOpt.get();
                d.setIsOn("ON".equalsIgnoreCase(action));
                deviceRepository.save(d);
                // Lưu log để sau này xem lại trên App
                deviceLogRepository.save(new DeviceLog(d.getId(), d.getName(), action, d.getRoom()));
            }

            return "OK: Sent " + action + " to " + fullMqttTopic;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }


    // --- API LẤY LỊCH SỬ (LOGS) ---
    @GetMapping("/logs/{deviceId}")
    public List<DeviceLog> getDeviceLogs(@PathVariable Long deviceId) {
        return deviceLogRepository.findByDeviceIdOrderByTimestampDesc(deviceId);
    }

    // --- CÁC API HỖ TRỢ KHÁC ---
    @GetMapping("/room/{roomName}")
    public List<Device> getDevicesByRoom(@PathVariable String roomName) {
        if ("All Rooms".equalsIgnoreCase(roomName) || "All".equalsIgnoreCase(roomName)) {
            return deviceRepository.findAll();
        }
        return deviceRepository.findByRoom(roomName);
    }

    private String removeAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    }
    @GetMapping("/{deviceId}/latest")
public DeviceTelemetry getLatestTelemetry(@PathVariable String deviceId) {
    // Phải đảm bảo deviceId nhận vào là "device_6" khớp với dữ liệu đã lưu ở bước 2
    return telemetryRepo.findTopByDeviceIdOrderByTimestampDesc(deviceId);
}
}
