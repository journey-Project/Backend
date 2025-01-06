package com.project.Journey.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSocketSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (WebSocket 요청은 CSRF 보호가 필요 없음)
                .csrf(csrf -> csrf.disable())
                // 요청에 대한 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ws/**","/topic/**", "/app/**","/WebSocketTest.html","/api/notification/**").permitAll() // WebSocket 엔드포인트는 인증 없이 접근 허용
                        .anyRequest().authenticated()        // 그 외 요청은 인증 필요
                );
        return http.build();
    }

    
}
