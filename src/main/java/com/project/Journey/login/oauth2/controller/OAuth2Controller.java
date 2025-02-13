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

    @Operation(
            summary = "소셜 회원가입 - 추가 정보 입력 페이지",
            description = """
                    [최초 소셜 로그인 = GUEST]인 경우, 
                    프론트에서 이 API(/api/oauth2/sign-up)를 호출하여 이메일 / 소셜타입 / 소셜ID를 표시하고 
                    일반 회원가입(/api/auth/sign-up) 로직으로 이어가도록 했습니다.
                    
                    - 'email' 필드는 자동으로 입력(수정 불가), 나머지 id/name/password 등을 추가 입력받아야합니다.
                    - 회원가입 성공하면 ROLE_USER로 전환
                    
                    ⚠ 주의: 
                    이 API는 단순 예시. 실제 가입 폼은 프론트에서 구현하고,
                    /api/auth/sign-up 호출로 DB에 등록합니다.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "소셜 회원가입 입력 페이지(데이터) 로드 성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "성공 예시",
                                            value = """
                                                    {
                                                      "email": "john.doe@example.com",
                                                      "socialType": "GOOGLE",
                                                      "socialId": "123456789",
                                                      "note": "status=GUEST 시 가입 필요"
                                                    }
                                                    """))),
            }
    )
    @GetMapping("/sign-up")
    public ResponseEntity<Map<String, String>> loadOauthSignup(
            @RequestParam String email,
            @RequestParam String socialType,
            @RequestParam String socialId
    ) {
        // 실제로는 email, socialType, socialId를 받아
        // 프론트엔드가 '회원가입 폼'에서 자동 입력 시키거나 추가 정보를 입력하도록 안내함.
        Map<String, String> response = new HashMap<>();
        response.put("email", email);
        response.put("socialType", socialType);
        response.put("socialId", socialId);
        response.put("note", "status=GUEST 시 이 값으로 회원가입 진행");

        return ResponseEntity.ok(response);
    }
}