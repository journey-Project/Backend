//package com.project.Journey.login.security.config;
//
//import com.project.Journey.login.security.filter.CustomAuthenticationFilter;
//import com.project.Journey.login.security.handler.CustomLoginFailureHandler;
//import com.project.Journey.login.security.handler.CustomLoginSuccessHandler;
//import com.project.Journey.login.security.provider.CustomAuthenticationProvider;
//import com.project.Journey.login.security.service.UserDetailsServiceImpl;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.web.cors.CorsConfiguration;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//@Order(2)
//public class WebSecurityConfig {
//
//    private final UserDetailsServiceImpl userDetailsService;
//    // (필요 없다면 주석 혹은 제거)
//    // private final OAuth2UserService oAuth2UserService;
//
//    //=== 화이트리스트 경로 ===
//    private static final String[] WHITELIST = {
//            "/",
//            "/login",
//            "/loginHome",
//            "/signUp",
//            "/renew",
//            "/loginSuccess",
//            "/login/oauth2/code/**",
//            "/oauth2/signUp",
//            "/error",
//            "/js/**",
//            // Swagger
//            "/swagger-ui",
//            "/v3/api-docs",
//            "/swagger-ui/index.html",
//            "/swagger-ui.html",
//            "/swagger-ui/**",
//            "/v3/api-docs/**",
//            // 기타
//            "/api/**",
//            "/WebSocketTest.html",
//            "/ws/**",
//            "/swagger-ui",
//            "/api/auth/sign-up",
//            "/api/oauth2/sign-up",
//            "/oauth2/**"
//    };
//
//    /**
//     * 비밀번호 암호화를 위한 BCryptPasswordEncoder
//     */
//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    /**
//     * 로그인 성공 시 핸들러 (JSON 응답)
//     */
//    @Bean
//    public CustomLoginSuccessHandler customLoginSuccessHandler() {
//        return new CustomLoginSuccessHandler();
//    }
//
//    /**
//     * 로그인 실패 시 핸들러 (JSON 응답)
//     */
//    @Bean
//    public CustomLoginFailureHandler customLoginFailureHandler() {
//        return new CustomLoginFailureHandler();
//    }
//
//    /**
//     * CustomAuthenticationProvider: UserDetailsService + PasswordEncoder
//     */
//    @Bean
//    public CustomAuthenticationProvider customAuthenticationProvider() {
//        return new CustomAuthenticationProvider(userDetailsService, passwordEncoder());
//    }
//
//    /**
//     * AuthenticationManager 빈 등록
//     */
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//        return http.getSharedObject(AuthenticationManagerBuilder.class)
//                .authenticationProvider(customAuthenticationProvider())
//                .build();
//    }
//
//    /**
//     * 커스텀 인증 필터 등록
//     */
//    @Bean
//    public CustomAuthenticationFilter customAuthenticationFilter(HttpSecurity http) throws Exception {
//        // AuthenticationManager 주입
//        CustomAuthenticationFilter filter = new CustomAuthenticationFilter(authenticationManager(http));
//
//        // "/login" + POST 요청일 때만 동작
//        filter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));
//
//        // 성공/실패 핸들러 설정
//        filter.setAuthenticationSuccessHandler(customLoginSuccessHandler());
//        filter.setAuthenticationFailureHandler(customLoginFailureHandler());
//
//        // 필수 초기화
//        filter.afterPropertiesSet();
//        return filter;
//    }
//
//    /**
//     * Spring Security Filter Chain
//     */
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        // CORS 설정
//        http.cors(cors -> cors.configurationSource(request -> {
//            CorsConfiguration config = new CorsConfiguration();
//            config.setAllowCredentials(true);
//
//            // 필요한 Origin 허용
//            config.addAllowedOriginPattern("https://*.journeysite.site");
//            config.addAllowedOrigin("http://localhost:5173");
//            config.addAllowedOrigin("https://dxkiwmo9p9ise.cloudfront.net");
//            // etc...
//
//            config.addAllowedHeader("*");
//            config.addAllowedMethod("*");
//            return config;
//        }));
//
//        // CSRF 비활성화
//        http.csrf(csrf -> csrf.disable());
//
//        // 경로별 접근 권한
//        http.authorizeHttpRequests(authz -> authz
//                .requestMatchers(WHITELIST).permitAll()
//                .anyRequest().authenticated()
//        );
//
//        // **세션을 사용하는 설정** (IF_REQUIRED)
//        http.sessionManagement(session ->
//                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//        );
//
//        // formLogin(), httpBasic() 비활성화
//        // 커스텀 인증 필터에서 /login 처리
//        http.formLogin(form -> form.disable());
//        http.httpBasic(httpBasic -> httpBasic.disable());
//
//        // 로그아웃
//        http.logout(logout -> logout
//                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
//                .logoutSuccessUrl("/")
//                .invalidateHttpSession(true)
//                .deleteCookies("JSESSIONID")
//        );
//
//        // 커스텀 인증 필터 추가
//        http.addFilterBefore(customAuthenticationFilter(http), UsernamePasswordAuthenticationFilter.class);
//
//        // (옵션) 소셜 로그인 세션 기반 사용 시
//        // http.oauth2Login(oauth -> oauth
//        //         .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
//        //         .successHandler(customOAuth2LoginSuccessHandler())
//        //         .failureHandler(customOAuth2LoginFailureHandler())
//        // );
//
//        return http.build();
//    }
//}
