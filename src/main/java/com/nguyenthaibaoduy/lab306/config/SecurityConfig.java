package com.nguyenthaibaoduy.lab306.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration; // Cần import
import org.springframework.web.cors.CorsConfigurationSource; // Cần import
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // Cần import
import java.util.Arrays; // Cần import

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable()) 
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // <-- THÊM CẤU HÌNH CORS
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() 
            );

        return http.build();
    }
    
    // ĐỊNH NGHĨA BEAN CẤU HÌNH CORS
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 1. Cho phép nguồn gốc (Origin): Cổng Flutter Web (hoặc *)
        // Sử dụng "*" để cho phép mọi nguồn gốc trong môi trường phát triển
        configuration.setAllowedOrigins(Arrays.asList("*")); 
        
        // 2. Cho phép phương thức (Methods): GET, POST, v.v.
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); 
        
        // 3. Cho phép tiêu đề (Headers):
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type")); 

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Áp dụng cấu hình CORS cho TẤT CẢ các URL ("/**")
        source.registerCorsConfiguration("/**", configuration); 
        
        return source;
    }
}