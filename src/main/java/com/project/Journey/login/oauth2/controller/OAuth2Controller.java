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
import java.util.Map;

/**
 * 소셜 로그인 (OAuth2) 전용 Controller
 *
 * - 프론트에서 이미 각 소셜 로그인 과정을 거쳐 인가 코드(code)를 받은 뒤,
 *   그 code를 백엔드로 전달해 JWT 쿠키를 발급받는 구조입니다.
 * - 추가적인 일반 회원가입(아이디/비번)을 별도로 하지 않고,
 *   소셜 사용자라면 바로 DB 등록(MemberRole=USER) 후 로그인 토큰을 발급합니다.
 */
@Tag(name = "소셜 로그인 (OAuth2)", description = "소셜 로그인: 인가 코드를 백엔드에 전달하여 JWT 발급")
@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final OAuth2UserServiceImpl oAuth2UserService;

    /**
     * 소셜 로그인: 프론트가 소셜에서 받은 인가 코드를 이 엔드포인트로 전달합니다.
     *
     * (provider) : "kakao", "naver", "google"
     * (code)     : 소셜 로그인 콜백에서 받은 인가 코드
     *
     * 이 API 호출 시:
     *  1) 백엔드가 소셜 서버에 code 교환 → access_token 획득
     *  2) 소셜 회원 정보(이메일, 프로필 등) 조회
     *  3) DB에 등록되어 있지 않다면 새로 생성(회원가입 대체)
     *     - role=USER 상태로 가입
     *  4) JWT(AccessToken, RefreshToken) 발급 → 쿠키로 내려줌
     *  5) JSON Response로 status("NEW_USER" or "EXIST"), email, role 등 반환
     *
     * [쿠키 설정]
     * - AccessToken: (로컬테스트 시 HttpOnly=false), SameSite=None
     * - RefreshToken: (로컬테스트 시 HttpOnly=false 권장 or 선택), SameSite=None
     * - 운영 배포 시 Secure + HttpOnly 설정
     *
     * 프론트:
     * - 이 API 응답 후, 쿠키에 JWT가 저장되므로 향후 API 요청 시 자동으로 인증됨
     * - status가 "NEW_USER"라면 즉시 로그인된 상태이긴 하지만, 추가 정보가 필요할 수 있음
     */
    @Operation(
            summary = "소셜 로그인 (인가 코드 수신)",
            description = """
                    소셜(카카오/네이버/구글) 로그인 완료 후 받은 인가코드를 백엔드로 전달해주시면,
                    백엔드가 소셜 서버와 토큰 교환 후 JWT 쿠키(Access/Refresh)를 발급하도록 되어있습니다.
                    
                    - provider: "kakao", "naver", "google"
                    - code: 소셜에서 받은 인가코드
                    - 응답: JSON(body) + JWT 쿠키(Set-Cookie 헤더)
                    
                    status 값 설명:
                    1) "NEW_USER": 기존에 없던 소셜 사용자 → 새로 DB 저장
                    2) "EXIST": 이미 등록된 소셜 사용자
                    
                    응답 시 Set-Cookie로 accessToken, refreshToken이 발급. 프론트 쪽에서 withCredentials 또는 credentials:'include' 설정 필수입니다!
                   
                    
                    """,
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "소셜 로그인 결과 (JWT 쿠키 + JSON)",
                            content = @Content(mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "NEW_USER 예시",
                                                    value = """
                                                        {
                                                          "status": "NEW_USER",
                                                          "message": "소셜 로그인 성공",
                                                          "email": "newuser@domain.com",
                                                          "role": "USER"
                                                        }
                                                        """
                                            ),
                                            @ExampleObject(
                                                    name = "EXIST 예시",
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
}