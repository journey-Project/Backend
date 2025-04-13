package com.project.Journey.login.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "로그인 및 로그아웃 관련 API")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    @Getter @Setter
    @Schema(description = "로그인 요청 DTO")
    public static class LoginRequest {
        @Schema(description = "로그인 ID(로그인 할 때 쓰는 아이디임)", example = "user123")
        private String loginId;

        @Schema(description = "비밀번호(6자 이상 필요)", example = "password123")
        private String password;
    }

    @Operation(
            summary = "로그인 API",
            description = "사용자의 아이디와 비밀번호로 로그인을 시도합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(example = "로그인 성공"))),
            @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호 오류", content = @Content(schema = @Schema(example = "아이디 또는 비밀번호가 틀렸습니다."))),
            @ApiResponse(responseCode = "403", description = "계정 잠김 또는 비활성화(임시)", content = @Content(schema = @Schema(example = "계정 에러")))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.getLoginId(), request.getPassword());

        try {
            // 인증 시도 → UserDetailsServiceImpl.loadUserByUsername() 호출
            Authentication authentication = authenticationManager.authenticate(authToken);

            // 성공 시 SecurityContextHolder와 세션에 인증정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
            session.setAttribute(
                    org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );

            // 예시: 반환 메시지
            return ResponseEntity.ok("로그인 성공");

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("아이디 또는 비밀번호가 틀렸습니다.");
        } catch (LockedException e) {
            return ResponseEntity.status(403).body("계정이 잠겨있습니다.");
        } catch (DisabledException e) {
            return ResponseEntity.status(403).body("비활성화된 계정입니다.");
        }
        // etc...
    }

    @Operation(
            summary = "로그아웃",
            description = """
        현재 로그인된 사용자의 세션을 무효화하여 로그아웃합니다.<br><br>

       응답:
        - 200: 세션 종료 성공
        """
    )
    @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content(schema = @Schema(example = "{\"message\": \"로그아웃 완료\"}")))

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("로그아웃 완료");
    }
}
