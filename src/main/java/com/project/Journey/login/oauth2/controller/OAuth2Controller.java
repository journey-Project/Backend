package com.project.Journey.login.oauth2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OAuth2Controller {

    // Oauth2 로그인 시 최초 로그인인 경우 회원가입 진행, 필요한 정보를 쿼리 파라미터로 받음
    @GetMapping("/oauth2/signUp")
    public String loadOauthSignup(@RequestParam String email, @RequestParam String socialType, @RequestParam String socialId, Model model) {
        model.addAttribute("email", email);
        model.addAttribute("socialType", socialType);
        model.addAttribute("socialId", socialId);
        return "templates/member/signUp";
    }
}