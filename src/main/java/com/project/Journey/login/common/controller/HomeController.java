package com.project.Journey.login.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "공통 페이지 컨트롤러", description = "메인 홈/회원가입/로그인 페이지 반환")
@Controller
public class HomeController {

    @Operation(summary = "홈 화면", description = "메인 페이지 리턴 (View/JSP/HTML)")
    @GetMapping("/")
    public String home() {
        return "home";
    }

    @Operation(summary = "회원가입 페이지", description = "회원가입 폼 (View)")
    @GetMapping("/signUp")
    public String loadSignUp() {
        return "member/signUp";
    }

    @Operation(summary = "로그인 홈", description = "로그인 폼 (View)")
    @GetMapping("/loginHome")
    public String loginHome() {
        return "member/loginHome";
    }

    @Operation(summary = "로그인 성공 페이지", description = "로그인 성공 시 이동하는 페이지")
    @GetMapping("/loginSuccess")
    public String loginSuccess() {
        return "member/loginSuccess";
    }
}
