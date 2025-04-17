package com.project.Journey.login.oauth2.controller;

import com.project.Journey.login.auth.CustomUserDetails;
import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.oauth2.service.OAuth2UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Collections;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth2")
@Tag(name = "소셜 로그인", description = "OAuth2 기반 소셜 로그인 API (현재 카카오만 구현), 세션방식 구현완료, 테스트 완료. ")
public class OAuth2Controller {

    private final OAuth2UserService oAuth2UserService;

    /**
     * 인가코드를 프론트에서 받아서, 아래 API로 전달
     * 예: POST /api/oauth2/callback?provider=kakao&code=AAAABBBB
     */
    @Operation(
            summary = "소셜 로그인 콜백 처리",
            description = """
        프론트에서 받은 인가 코드를 통해 소셜 로그인 처리를 수행합니다.<br><br>
        
        현재는 카카오(Kakao)만 구현되어 있습니다.<br>
        로그인에 성공하면, 서버는 세션을 생성하고 JSESSIONID 쿠키를 반환합니다.<br>
        이후 요청에서는 이 세션 쿠키를 통해 인증이 유지됩니다. (세션 기반 인증)<br><br>

        요청 예시:<br>
        POST /api/oauth2/kakao?code=ABCDEF123456<br><br>

        요청 파라미터:<br>
        - <code>provider</code>: 소셜 플랫폼 이름 (예: kakao)<br>
        - <code>code</code>: 인가 코드 (프론트에서 받은 OAuth2 code)
        """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "소셜 로그인 성공",
                    content = @Content(schema = @Schema(example = "소셜 로그인 성공! Email=test@kakao.com"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "로그인 실패",
                    content = @Content(schema = @Schema(example = "소셜 로그인 실패: 유효하지 않은 인가 코드입니다."))
            )
    })
    @PostMapping("/{provider}")
    public ResponseEntity<?> socialLoginCallback(
            @Parameter(description = "소셜 로그인 제공자 (kakao 또는 naver)", example = "kakao")
            @RequestParam String provider,

            @Parameter(description = "프론트에서 받은 인가 코드", example = "abc123xyz456")
            @RequestParam String code,
            HttpSession session
    ) {
        try {
            Member member = oAuth2UserService.getOrCreateSocialUser(provider, code);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            new CustomUserDetails(member),  // principal
                            null,                           // credentials (소셜은 PW 없음)
                            Collections.singletonList(
                                    new SimpleGrantedAuthority("ROLE_" + member.getRole())
                            )
                    );
            SecurityContextHolder.getContext().setAuthentication(authToken);

            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );

            log.info("[OAuth2Controller] 소셜 로그인 성공: {}", member.getEmail());


            return ResponseEntity.ok("소셜 로그인 성공! Email=" + member.getEmail());

        } catch (Exception e) {
            log.error("소셜 로그인 실패", e);
            return ResponseEntity.badRequest().body("소셜 로그인 실패: " + e.getMessage());
        }
    }
}
