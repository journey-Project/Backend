package com.project.Journey.login.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "공통 페이지 API", description = "홈, 회원가입, 로그인 관련 엔드포인트")
@RestController
@RequestMapping("/api/pages")
public class HomeController {

    @Operation(summary = "홈 화면 데이터", description = "홈 화면 내용을 반환합니다.")
    @GetMapping("/home")
    public String getHome() {
        return "{\"message\": \"Welcome to the Home Page!\"}";
    }

    @Operation(summary = "회원가입 페이지 데이터", description = "회원가입 페이지 내용을 반환합니다.")
    @GetMapping("/signUp")
    public String getSignUpPage() {
        return "{\"message\": \"Sign-Up Page\"}";
    }

    @Operation(summary = "로그인 페이지 데이터", description = "로그인 페이지 내용을 반환합니다.")
    @GetMapping("/loginHome")
    public String getLoginPage() {
        return "{\"message\": \"Login Page\"}";
    }
}
