package com.nguyenthaibaoduy.lab306.mqtt;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttGateway {
    // Hàm này để Controller gọi
    void sendToMqtt(String data, @Header(MqttHeaders.TOPIC) String topic);
}