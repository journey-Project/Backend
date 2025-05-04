package com.project.Journey.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Journey API Documentation")
                        .description("여정(Journey) 프로젝트의 전체 API 문서입니다. 로그인, 회원가입, 게시판 등 기능 포함.")
                        .version("v1.0.0")
                        .contact(new Contact().name("Journey Dev Team").email("sprauncy76@gmail.com"))
                );
    }
}