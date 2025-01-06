package com.project.Journey.login.security.config;

import com.project.Journey.login.jwt.service.JwtService;
import com.project.Journey.login.oauth2.handler.CustomOAuth2LoginFailureHandler;
import com.project.Journey.login.oauth2.handler.CustomOAuth2LoginSuccessHandler;
import com.project.Journey.login.oauth2.service.OAuth2UserServiceImpl;
import com.project.Journey.login.security.filter.CustomAuthenticationFilter;
import com.project.Journey.login.security.handler.CustomLoginFailureHandler;
import com.project.Journey.login.security.handler.CustomLoginSuccessHandler;
import com.project.Journey.login.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final OAuth2UserServiceImpl oAuth2UserService;
    private final JwtService jwtService;

    private static final String[] SWAGGER_WHITELIST = {"/", "/login", "/loginHome", "/signUp", "/renew", "/loginSuccess",
            "/login/oauth2/code/**", "/oauth2/signUp", "/error", "/js/**", // Swagger UI & Docs
            "/swagger-ui", "/v3/api-docs", "/swagger-ui/index.html", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**"
            ,"/api/**", "/WebSocketTest.html","/ws/**"};


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomLoginSuccessHandler customLoginSuccessHandler(){
        return new CustomLoginSuccessHandler(jwtService);
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(userDetailsService, passwordEncoder());
    }

    @Bean
    public CustomLoginFailureHandler customLoginFailureHandler() {
        return new CustomLoginFailureHandler();
    }

    @Bean
    public CustomOAuth2LoginSuccessHandler customOAuth2LoginSuccessHandler(){
        return new CustomOAuth2LoginSuccessHandler(jwtService);
    }

    @Bean
    public CustomOAuth2LoginFailureHandler customOAuth2LoginFailureHandler() {
        return new CustomOAuth2LoginFailureHandler();
    }

    // AuthenticationManager 를 HttpSecurity 로부터 빌드하여 Bean 으로 등록
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(customAuthenticationProvider())
                .build();
    }

    // CustomAuthenticationFilter 에 AuthenticationManager 주입
    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager(http));
        customAuthenticationFilter.setFilterProcessesUrl("/login");
        customAuthenticationFilter.setAuthenticationSuccessHandler(customLoginSuccessHandler());
        customAuthenticationFilter.setAuthenticationFailureHandler(customLoginFailureHandler());
        customAuthenticationFilter.afterPropertiesSet();
        return customAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(SWAGGER_WHITELIST).permitAll() // Swagger 경로 허용
                        .anyRequest().authenticated() // 나머지 경로는 인증 요구
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/swagger-ui", "/v3/api-docs", "/swagger-ui/**", "/v3/api-docs/**").disable())
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                        .successHandler(customOAuth2LoginSuccessHandler())
                        .failureHandler(customOAuth2LoginFailureHandler())
                );

        // CustomAuthenticationFilter 등록
        http.addFilterBefore(customAuthenticationFilter(http), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}