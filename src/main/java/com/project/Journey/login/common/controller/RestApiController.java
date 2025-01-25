package com.project.Journey.login.common.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RestApiController {

    @GetMapping("/user")
    public Map<String, String> getUser() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "user 입니다");
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public Map<String, String> getAdmin() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "admin 입니다");
        return response;
    }
}