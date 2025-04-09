package com.project.Journey.login.auth;

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
public class AuthController {

    private final AuthenticationManager authenticationManager;

    @Getter @Setter
    public static class LoginRequest {
        private String loginId;
        private String password;
    }

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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("로그아웃 완료");
    }
}
