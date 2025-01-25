package com.project.Journey.login.member.controller;

import com.project.Journey.login.member.domain.MemberDTO;
import com.project.Journey.login.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "회원가입 및 회원 관리", description = "회원 정보 등록, 조회 등 처리")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원가입 API", description = "새로운 사용자를 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원가입 성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{ \"status\": \"success\", \"message\": \"회원가입이 완료되었습니다.\" }"))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{ \"status\": \"error\", \"message\": \"유효성 검사 실패\" }"))),
                    @ApiResponse(responseCode = "409", description = "중복된 회원 정보",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{ \"status\": \"error\", \"message\": \"이미 존재하는 아이디입니다.\" }")))
            })
    @PostMapping("/api/auth/signUp")
    public ResponseEntity<Map<String, Object>> signUp(@Valid @RequestBody MemberDTO memberDTO, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();

        // 유효성 검사 실패 처리
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            response.put("status", "error");
            response.put("message", "유효성 검사 실패");
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }

        // 아이디 중복 체크
        if (memberService.findById(memberDTO.getId()).isPresent()) {
            response.put("status", "error");
            response.put("message", "이미 존재하는 아이디입니다.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        // 이메일 중복 체크
        if (memberService.findByEmail(memberDTO.getEmail()).isPresent()) {
            response.put("status", "error");
            response.put("message", "이미 존재하는 이메일입니다.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        // 회원가입 성공
        memberService.save(memberDTO);
        response.put("status", "success");
        response.put("message", "회원가입이 완료되었습니다.");
        return ResponseEntity.ok(response);
    }
}