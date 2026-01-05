package com.nguyenthaibaoduy.lab306.repository;

import com.nguyenthaibaoduy.lab306.model.DeviceTelemetry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelemetryRepository extends JpaRepository<DeviceTelemetry, Long> {
    // Spring tự sinh code SQL
     DeviceTelemetry findTopByDeviceIdOrderByTimestampDesc(String deviceId);
}

