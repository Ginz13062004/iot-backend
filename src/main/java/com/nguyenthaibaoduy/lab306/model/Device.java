package com.nguyenthaibaoduy.lab306.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;       // Ví dụ: "Đèn Led"
    private String topic;      // Ví dụ: "phongkhach/denled"
    private String room;       // Ví dụ: "Phòng Khách"
    private String type;       // Ví dụ: "Light" hoặc "Fan"
    private String imageAsset; // Ví dụ: "assets/den.png"

    @Column(name = "is_on")
    private Boolean isOn = false; // Mặc định là OFF khi mới tạo

    // Thiết lập quan hệ Many-to-One với User để tránh lỗi JdbcType
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore // Ngăn lỗi vòng lặp JSON khiến server bị treo
    private User user;

    // --- CONSTRUCTOR ---
    public Device() {}

    // --- GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getImageAsset() { return imageAsset; }
    public void setImageAsset(String imageAsset) { this.imageAsset = imageAsset; }

    // Sử dụng tên hàm setIsOn để khớp với logic trong DeviceController mới
    public Boolean getIsOn() { return isOn != null ? isOn : false; }
    public void setIsOn(Boolean isOn) { this.isOn = isOn; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}