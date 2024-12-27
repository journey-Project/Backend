//package com.project.Journey.board.config;
//
//import jakarta.servlet.DispatcherType;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class SecurityConfig {
//
//    private static final String[] AUTH_WHITELIST = {
//            "/", "/login", "/signUp", "/renew", "/loginSuccess",
//            "/login/oauth2/code/**", "/oauth2/signUp", "/error", "/js/**",
//            // Swagger UI & Docs
//            "/swagger-ui/**", "/v3/api-docs/**"
//    };
//
//    @Bean(name = "defaultSecurityFilterChain")
//    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        return httpSecurity
//                .authorizeHttpRequests(authorize -> authorize
//                        .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.ASYNC).permitAll()
//                        .requestMatchers(AUTH_WHITELIST).permitAll() // 화이트리스트 경로 인증 없이 접근 가능
//                        .anyRequest().permitAll() // 나머지 요청도 인증 없이 접근 가능
//                )
//                .csrf(CsrfConfigurer::disable) // CSRF 비활성화
//                .cors(Customizer.withDefaults()) // CORS 기본 설정
//                .formLogin(form -> form.disable()) // 기본 로그인 폼 비활성화
//                .httpBasic(http -> http.disable()) // HTTP Basic 인증 비활성화
//                .build();
//    }
//}