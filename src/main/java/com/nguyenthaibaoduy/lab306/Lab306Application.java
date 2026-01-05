package com.nguyenthaibaoduy.lab306;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.TimeZone; // Nhớ import dòng này

@SpringBootApplication
public class Lab306Application {

    public static void main(String[] args) {
        // DÒNG QUAN TRỌNG NHẤT: Ép buộc toàn bộ ứng dụng dùng múi giờ chuẩn
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        
        SpringApplication.run(Lab306Application.class, args);
    }
}