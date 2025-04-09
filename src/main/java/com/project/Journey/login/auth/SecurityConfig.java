package com.project.Journey.login.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Order(2)
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    // === 화이트리스트 경로 (예시) ===
    private static final String[] WHITELIST = {
            "/",
            "/login",
            "/loginHome",
            "/signUp",
            "/renew",
            "/loginSuccess",
            "/login/oauth2/code/**",
            "/oauth2/signUp",
            "/error",
            "/js/**",
            // Swagger
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-ui/index.html",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            // 기타
            "/api/**",
            "/WebSocketTest.html",
            "/ws/**",
            "/swagger-ui",
            "/api/auth/sign-up",
            "/api/oauth2/sign-up",
            "/oauth2/**"
    };

    /**
     * 비밀번호 암호화를 위한 Bean
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager Bean 등록
     * DaoAuthenticationProvider를 자동으로 구성하여
     * UserDetailsService + PasswordEncoder를 사용
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        // 예전처럼 .and()를 쓰지 않고 빌더 객체를 직접 받아 build() 합니다
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return builder.build();
    }

    /**
     * 메인 Security FilterChain 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // -- (A) CORS 설정
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // -- (B) CSRF 설정
        // REST API 로만 사용 시 대체로 disable
        // 만약 폼 전송(CSRF 토큰)이 필요하다면 enable
        http.csrf(csrf -> csrf.disable());

        // -- (C) 세션 정책
        // 기본적으로 로그인 시 세션을 생성( IF_REQUIRED )
        http.sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                // 필요 시 세션고정공격 방지, 만료처리, 등등
        );

        // -- (D) URL 보안
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(WHITELIST).permitAll()
                .anyRequest().authenticated()
        );

        // -- (E) formLogin, httpBasic 비활성(REST 방식을 가정)
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());

        // -- (F) 로그아웃
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
        );

        // 최종 빌드
        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // (1) 허용 origin
        // - 특정 도메인/포트, 혹은 패턴
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "https://dxkiwmo9p9ise.cloudfront.net"
        ));
        // (2) 허용 메서드
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // (3) 기타
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // 세션 쿠키 전송 허용
        config.setMaxAge(3600L);         // Pre-flight 캐싱 시간(초)

        // CORS 매핑
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로에 대해 위 설정 적용
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
