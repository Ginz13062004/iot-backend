package com.nguyenthaibaoduy.lab306.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "device_telemetry")
public class DeviceTelemetry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;
    private Double power;   // Công suất (W)
    private Double current; // Dòng điện (A) - MỚI
    private Double voltage; // Điện áp (V) - MỚI
    private Double energy;  // Điện năng (kWh) - MỚI
    private String state;   // Trạng thái ON/OFF
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    // --- GETTER & SETTER ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public Double getPower() { return power; }
    public void setPower(Double power) { this.power = power; }

    public Double getCurrent() { return current; }
    public void setCurrent(Double current) { this.current = current; }

    public Double getVoltage() { return voltage; }
    public void setVoltage(Double voltage) { this.voltage = voltage; }

    public Double getEnergy() { return energy; }
    public void setEnergy(Double energy) { this.energy = energy; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}