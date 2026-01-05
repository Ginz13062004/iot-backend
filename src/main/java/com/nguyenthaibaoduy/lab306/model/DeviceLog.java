package com.nguyenthaibaoduy.lab306.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "device_logs")
public class DeviceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long deviceId;
    private String deviceName;
    private String action;    // "ON" hoặc "OFF"
    private String room;
    private LocalDateTime timestamp;

    public DeviceLog() {}

    public DeviceLog(Long deviceId, String deviceName, String action, String room) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.action = action;
        this.room = room;
        this.timestamp = LocalDateTime.now();
    }

    // --- GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}