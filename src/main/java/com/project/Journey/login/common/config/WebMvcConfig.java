package com.project.Journey.login.common.config;

import com.project.Journey.login.common.interceptor.AccessPermissionCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AccessPermissionCheckInterceptor())
                .order(1)
                .excludePathPatterns("/", "/login", "/loginHome", "/signUp", "/renew", "/loginSuccess",
                        "/login/oauth2/code/**", "/oauth2/signUp", "/error", "/js/**", "/swagger-ui", "/v3/api-docs","/swagger-ui/index.html", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html");
    }

}
