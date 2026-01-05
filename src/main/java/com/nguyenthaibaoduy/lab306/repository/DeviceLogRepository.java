package com.nguyenthaibaoduy.lab306.repository;

import com.nguyenthaibaoduy.lab306.model.DeviceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeviceLogRepository extends JpaRepository<DeviceLog, Long> {
    // Tìm lịch sử của một thiết bị cụ thể và xếp cái mới nhất lên đầu
    List<DeviceLog> findByDeviceIdOrderByTimestampDesc(Long deviceId);
}