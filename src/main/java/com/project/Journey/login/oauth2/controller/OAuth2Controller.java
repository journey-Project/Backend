package com.project.Journey.login.oauth2.controller;

import com.project.Journey.login.oauth2.service.OAuth2UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 소셜 로그인 (프론트에서 code를 먼저 받고, 백엔드로 전달하는 방식)
 */
@Tag(name = "소셜 로그인 (OAuth2)", description = "소셜 로그인 시, 인가 코드를 백엔드로 전달받아 토큰 교환 후 JWT 발급")
@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final OAuth2UserServiceImpl oAuth2UserService;

    /**
     * 소셜 로그인: 프론트가 인가 코드를 받아, 백엔드에 전달
     * (provider 예: "kakao", "naver", "google")
     */
    @Operation(
            summary = "소셜 로그인 코드 수신",
            description = """
                    이 엔드포인트는 프론트가 소셜 로그인 완료 후 받은 인가 코드를 백엔드로 전달할 때 사용합니다.
                    
                    1) 프론트:
                       - 소셜(카카오/네이버/구글) 로그인 성공 → redirect_uri(프론트 콜백)로 code 수신
                       - code를 추출 후, POST(/api/oauth2/{provider}?code=xxx) 로 백엔드에 전송
                    
                    2) 백엔드:
                       - WebClient로 소셜 서버에 토큰 교환 요청 → access_token, refresh_token 획득
                       - 사용자 정보 조회 → DB 확인 (GUEST/EXIST)
                       - EXIST면 JWT(쿠키) 발급 + JSON 응답(status=EXIST)
                       - GUEST면 DB 임시등록 + JSON 응답(status=GUEST, email, socialId)
                    
                    응답 예시:
                    
                    - GUEST:
                      {
                        "status": "GUEST",
                        "message": "소셜 최초 로그인",
                        "email": "user@example.com",
                        "socialType": "KAKAO",
                        "socialId": "1234567890"
                      }
                    
                    - EXIST (쿠키에 accessToken, refreshToken 발급됨):
                      {
                        "status": "EXIST",
                        "message": "소셜 로그인 성공",
                        "email": "user@example.com",
                        "role": "USER"
                      }
                    
                    ⚠ 쿠키 설정:
                       - accessCookie: HttpOnly=false
                       - refreshCookie: HttpOnly=true
                       - path="/" 로 전역 쿠키
                    """,
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "로그인 결과 JSON 반환 (status=GUEST or EXIST)",
                            content = @Content(mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "최초로그인(GUEST) 예시",
                                                    value = """
                                                        {
                                                          "status": "GUEST",
                                                          "message": "소셜 최초 로그인",
                                                          "email": "newuser@domain.com",
                                                          "socialType": "KAKAO",
                                                          "socialId": "123456789"
                                                        }
                                                        """
                                            ),
                                            @ExampleObject(
                                                    name = "기존회원(EXIST) 예시",
                                                    value = """
                                                        {
                                                          "status": "EXIST",
                                                          "message": "소셜 로그인 성공",
                                                          "email": "existing@domain.com",
                                                          "role": "USER"
                                                        }
                                                        """
                                            )
                                    }
                            )
                    )
            }
    )
    @PostMapping("/{provider}")
    public ResponseEntity<Map<String, Object>> socialLogin(
            @PathVariable String provider,
            @RequestParam String code,
            HttpServletResponse response
    ) {
        Map<String, Object> result = oAuth2UserService.oauthLogin(provider, code, response);
        return ResponseEntity.ok(result);
    }


    /**
     * (기존) 소셜 회원가입 - 추가 정보 입력 페이지
     * [최초 소셜 로그인 = GUEST]인 경우, 프론트에서 이 API(/api/oauth2/sign-up) 호출하여
     * 이메일 / 소셜타입 / 소셜ID를 표시, 일반 회원가입(/api/auth/sign-up)으로 진행.
     */
    @Operation(
            summary = "소셜 회원가입 - 추가 정보 입력 (GUEST)",
            description = """
                GUEST 상태에서 프론트가 email, socialType, socialId를
                쿼리 파라미터로 받아와서 회원가입 폼 자동 입력에 사용.
                
                - email은 수정 불가(소셜 정보로부터 받은 것이므로)
                - 일반 회원가입(/api/auth/sign-up) 호출 시, 해당 email을 사용
            """,
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "추가 정보 페이지 로드 성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "성공 예시",
                                            value = """
                                                    {
                                                      "email": "john.doe@example.com",
                                                      "socialType": "GOOGLE",
                                                      "socialId": "123456789",
                                                      "note": "status=GUEST 시 가입 필요"
                                                    }
                                                    """)))
            }
    )
    @GetMapping("/sign-up")
    public ResponseEntity<Map<String, String>> loadOauthSignup(
            @RequestParam String email,
            @RequestParam String socialType,
            @RequestParam String socialId
    ) {
        Map<String, String> response = new HashMap<>();
        response.put("email", email);
        response.put("socialType", socialType);
        response.put("socialId", socialId);
        response.put("note", "status=GUEST 시 이 값으로 회원가입 진행");
        return ResponseEntity.ok(response);
    }
}