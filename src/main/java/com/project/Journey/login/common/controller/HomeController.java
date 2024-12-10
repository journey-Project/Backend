package com.project.Journey.login.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/signUp")
    public String loadSignUp() {
        return "member/signUp";
    }

    @GetMapping("/loginHome")
    public String loginHome() {
        return "member/loginHome";
    }

    @GetMapping("/loginSuccess")
    public String loginSuccess() {
        return "member/loginSuccess";
    }
}
