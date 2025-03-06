    package com.project.Journey.login.common.config;

    import com.project.Journey.login.common.interceptor.AccessPermissionCheckInterceptor;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.web.servlet.config.annotation.CorsRegistry;
    import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
    import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new AccessPermissionCheckInterceptor())
//                .order(1)
//                .excludePathPatterns("/", "/login", "/loginHome", "/signUp", "/renew", "/loginSuccess",
//                        "/login/oauth2/code/**", "/oauth2/signUp", "/error", "/js/**", "/swagger-ui", "/v3/api-docs","/swagger-ui/index.html", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html");
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AccessPermissionCheckInterceptor())
                .order(1)
                .excludePathPatterns("/", "/login", "/loginHome", "/signUp", "/renew", "/loginSuccess",
                        "/login/oauth2/code/**", "/oauth2/signUp", "/error", "/js/**", // Swagger UI & Docs
                        "/swagger-ui", "/v3/api-docs", "/swagger-ui/index.html", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**"
                        ,"/api/**", "/WebSocketTest.html","/ws/**","/swagger-ui", "/api/auth/sign-up", "/api/oauth2/sign-up", "/oauth2/**"); // 필요한 경로만 예외처리
    }



    // CORS 설정 추가
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해 CORS 설정
                .allowedOrigins("http://localhost:5173", "https://dxkiwmo9p9ise.cloudfront.net") // 프론트엔드 도메인만 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
                .allowCredentials(true); // 쿠키 허용
    }
}
