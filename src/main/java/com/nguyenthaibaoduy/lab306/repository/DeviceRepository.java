package com.nguyenthaibaoduy.lab306.repository;

import com.nguyenthaibaoduy.lab306.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // Nhớ import List

public interface DeviceRepository extends JpaRepository<Device, Long> {
    // Hàm tìm tất cả thiết bị trong 1 phòng cụ thể
    List<Device> findByRoom(String room);
}