package com.project.Journey.login.oauth2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "소셜 로그인 (OAuth2)", description = "소셜 로그인 시 추가 정보 입력을 위한 컨트롤러")
@RestController
@RequestMapping("/api/oauth2")
public class OAuth2Controller {

    // Oauth2 로그인 시 최초 로그인인 경우 회원가입 진행, 필요한 정보를 쿼리 파라미터로 받음
    @Operation(
            summary = "소셜 회원가입 - 추가 정보 입력 페이지",
            description = "최초 소셜 로그인 시, 아직 회원 정보가 DB에 없는 경우 추가 정보 입력이 필요합니다. " +
                    "이때 email, socialType, socialId를 쿼리 파라미터로 받아서 클라이언트가 추가 정보를 요청하거나 표시할 수 있습니다.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "추가 정보 페이지 로드 성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "성공 예시",
                                            value = "{\n  \"email\": \"john.doe@example.com\",\n  \"socialType\": \"GOOGLE\",\n  \"socialId\": \"123456789\"\n}"))),
            }
    )
    @GetMapping("/sign-up")
    public ResponseEntity<Map<String, String>> loadOauthSignup(
            @RequestParam String email,
            @RequestParam String socialType,
            @RequestParam String socialId
    ) {
        // 실제로는 email, socialType, socialId를 받아서
        // 추가 회원가입 로직을 진행하거나, 프론트엔드로 전달하는 역할
        Map<String, String> response = new HashMap<>();
        response.put("email", email);
        response.put("socialType", socialType);
        response.put("socialId", socialId);

        return ResponseEntity.ok(response);
    }
}