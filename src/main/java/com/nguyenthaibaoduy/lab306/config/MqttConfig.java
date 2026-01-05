package com.nguyenthaibaoduy.lab306.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttConfig {

    // 1. Thay thông tin Broker của bạn ở đây
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        
        // Nếu chạy local thì để tcp://localhost:1883
        // Nếu chạy HiveMQ thì đổi link vào đây
        options.setServerURIs(new String[] { "tcp://broker.emqx.io:1883" }); 
        options.setUserName("admin"); // Nếu không có pass thì xóa dòng này
        options.setPassword("123456".toCharArray()); // Nếu không có pass thì xóa dòng này
        
        factory.setConnectionOptions(options);
        return factory;
    }

    // --- KÊNH GỬI ĐI (OUTBOUND) ---
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        // ClientID gửi tin: server_sender
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("server_sender", mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("smarthome/general");
        return messageHandler;
    }

    // --- KÊNH NHẬN VỀ (INBOUND) ---
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        // ClientID nhận tin: server_receiver
        // Lắng nghe topic: smarthome/+/telemetry (+ là ký tự đại diện cho deviceID)
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter("server_receiver", mqttClientFactory(), "smarthome/+/telemetry");
        
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }
}