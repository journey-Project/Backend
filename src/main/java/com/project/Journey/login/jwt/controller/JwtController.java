//package com.project.Journey.login.jwt.controller;
//
//import com.project.Journey.login.jwt.constants.JwtConstants;
//import com.project.Journey.login.jwt.constants.JwtUtils;
//import com.project.Journey.login.jwt.service.JwtService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.NoSuchElementException;
//
//@Tag(name = "JWT 재발급", description = "Refresh Token 으로 Access Token 재발급")
//@RestController
//@RequiredArgsConstructor
//public class JwtController {
//
//    private final JwtService jwtService;
//
//    @Operation(summary = "Access Token 재발급 API", description = "Refresh Token을 통해 Access Token 을 재발급받습니다.")
//    @GetMapping("/renew")
//    public ResponseEntity<?> renewToken(HttpServletRequest request, HttpServletResponse response) {
//        try {
//            String refreshToken = JwtUtils.getTokenFromHeader(request.getHeader(JwtConstants.JWT_HEADER));
//            return ResponseEntity.ok(JwtConstants.JWT_TYPE + jwtService.renewToken(refreshToken));
//        } catch (NoSuchElementException e) {
//            return ResponseEntity.badRequest().body("Refresh Token 이 만료되었습니다");
//        } catch (UsernameNotFoundException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//
//}
