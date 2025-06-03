    package com.project.Journey.login.member.controller;

    import com.project.Journey.login.auth.CustomUserDetails;
    import com.project.Journey.login.member.domain.Member;
    import com.project.Journey.login.member.dto.MemberDTO;
    import com.project.Journey.login.member.dto.MemberInfoDTO;
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
    @Tag(name = "회원", description = "회원가입 및 회원 정보 수정 및 회원 정보 조회 API")
    public class MemberController {

        private final MemberRepository memberRepository;
        private final MemberService memberService;

        @Operation(
                summary = "회원가입",
                description = """
        일반 회원가입을 수행

        입력 필드:
        - loginId (string): 사용자가 로그인 시 사용할 ID (중복 불가)
        - name (string): 실명
        - password (string): 비밀번호 (6자 이상)
        - email (string): 이메일 주소 (중복 불가)
        - nickname (string, nullable): 닉네임 (실명으로 설정됨), 사용자가 가입할 때 설정안함. 자동으로 실명으로 디비에 저장됨.

        참고: loginId 또는 email이 중복되면 가입에 실패되도록 함.
        """
        )
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "가입 성공"),
                @ApiResponse(responseCode = "400", description = "중복된 아이디 또는 이메일, 혹은 잘못된 요청")
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
            - id (member 고유 id - PK)
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
            dto.setId(member.getId());
            dto.setLoginId(member.getLoginId());
            dto.setName(member.getName());
            dto.setNickname(member.getNickname());
            dto.setEmail(member.getEmail());
            dto.setProfileImage(member.getProfileImage());
            dto.setSocialType(member.getSocialType() != null ? member.getSocialType().name() : null);

            return ResponseEntity.ok(dto);
        }
    }
