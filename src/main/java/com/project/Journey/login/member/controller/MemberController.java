    package com.project.Journey.login.member.controller;

    import com.project.Journey.login.auth.CustomUserDetails;
    import com.project.Journey.login.member.domain.Member;
    import com.project.Journey.login.member.domain.MemberDTO;
    import com.project.Journey.login.member.domain.MemberInfoDTO;
    import com.project.Journey.login.member.repository.MemberRepository;
    import com.project.Journey.login.member.service.MemberService;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.responses.ApiResponse;
    import io.swagger.v3.oas.annotations.responses.ApiResponses;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.Authentication;
    import org.springframework.web.bind.annotation.*;

    import java.util.Map;

    @RestController
    @RequestMapping("/api/members")
    @RequiredArgsConstructor
    @Tag(name = "회원", description = "회원가입 및 회원 정보 수정 관련 API")
    public class MemberController {

        private final MemberRepository memberRepository;
        private final MemberService memberService;

        @Operation(summary = "회원가입", description = "일반 회원가입을 수행. 닉네임은 실명으로 초기값 설정하였음. 중복된 아이디/이메일 시 실패")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "가입 성공"),
                @ApiResponse(responseCode = "400", description = "중복 아이디/이메일 or 잘못된 요청임")
        })
        @PostMapping("/signup")
        public ResponseEntity<?> signUp(@RequestBody MemberDTO dto) {
            try {
                Long memberId = memberService.signUp(dto);
                return ResponseEntity.ok("회원가입 성공! memberId=" + memberId);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("회원가입 실패: " + e.getMessage());
            }
        }

        @Operation(summary = "닉네임 수정", description = """
            기존 회원의 닉네임을 수정합니다.<br>
            요청 조건:<br>
            - nickname: 새로운 닉네임 문자열 (빈 문자열 또는 null이면 실명으로 초기화됨)
            """)
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "닉네임 변경 성공"),
                @ApiResponse(responseCode = "400", description = "유효하지 않은 memberId")
        })
        @PatchMapping("/{memberId}/nickname")
        public ResponseEntity<?> updateNickname(@PathVariable Long memberId, @RequestBody Map<String, String> body) {
            try {
                String nickname = body.get("nickname");
                memberService.updateNickname(memberId, nickname);
                return ResponseEntity.ok("닉네임 변경 성공!");
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("닉네임 변경 실패: " + e.getMessage());
            }
        }

        @Operation(
                summary = "내 정보 조회",
                description = """
            세션 인증된 사용자 본인의 정보를 반환합니다.<br><br>
            
            응답:
            - loginId (로그인 ID)
            - name (실명)
            - nickname (닉네임)
            - email (이메일)
            - profileImage (프로필 이미지 URL)
            - socialType (소셜 로그인 플랫폼: KAKAO, NAVER 등)<br><br>
    
            """
        )
        @GetMapping("/me")
        public ResponseEntity<?> getMyInfo(Authentication authentication) {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("로그인되지 않은 사용자입니다.");
            }

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Member member = userDetails.getMember();

            MemberInfoDTO dto = new MemberInfoDTO();
            dto.setLoginId(member.getLoginId());
            dto.setName(member.getName());
            dto.setNickname(member.getNickname());
            dto.setEmail(member.getEmail());
            dto.setProfileImage(member.getProfileImage());
            dto.setSocialType(member.getSocialType() != null ? member.getSocialType().name() : null);

            return ResponseEntity.ok(dto);
        }
    }
