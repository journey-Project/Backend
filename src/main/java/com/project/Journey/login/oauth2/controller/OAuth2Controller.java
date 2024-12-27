package com.project.Journey.login.oauth2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "소셜 로그인 (OAuth2)", description = "소셜 로그인 시 추가 정보 입력을 위한 컨트롤러")

@Controller
public class OAuth2Controller {

    // Oauth2 로그인 시 최초 로그인인 경우 회원가입 진행, 필요한 정보를 쿼리 파라미터로 받음
    @Operation(summary = "소셜 회원가입 페이지", description = "최초 소셜 로그인 시, 필요한 정보 입력 페이지")
    @GetMapping("/oauth2/signUp")
    public String loadOauthSignup(@RequestParam String email, @RequestParam String socialType, @RequestParam String socialId, Model model) {
        model.addAttribute("email", email);
        model.addAttribute("socialType", socialType);
        model.addAttribute("socialId", socialId);
        return "member/signUp";
    }
}