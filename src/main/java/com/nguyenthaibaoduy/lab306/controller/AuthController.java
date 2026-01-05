package com.nguyenthaibaoduy.lab306.controller;

import com.nguyenthaibaoduy.lab306.model.User;
import com.nguyenthaibaoduy.lab306.repository.UserRepository;
import com.nguyenthaibaoduy.lab306.mqtt.MqttGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MqttGateway mqttGateway; // Để bắn thông báo ai đăng nhập

    // Đăng ký
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return "Username already exists";
        }
        userRepository.save(user);
        return "Success";
    }

    // Đăng nhập
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            // Gửi MQTT thông báo Admin biết có người đăng nhập (Yêu cầu số 3 của bạn)
            String logMsg = "User [" + username + "] just logged in via App";
            mqttGateway.sendToMqtt(logMsg, "admin/logs");
            
            return "Login Success";
        }
        return "Invalid credentials";
    }
    // 👇 THÊM HÀM NÀY ĐỂ XEM DANH SÁCH USER 👇
    @GetMapping("/all-users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
}
}