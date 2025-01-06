package com.project.Journey.login.common.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Journey.login.member.domain.MemberRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;

/**
 * 권한에 따른 처리를 하기 위한 Interceptor
 * @EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) 를 선언하고
 * Controller 에서 @PreAuthorize 를 사용해도 되지만, Controller 로 가기 전, Interceptor 에서 처리를 하는 것이 낫다고 생각함
 */

public class AccessPermissionCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        // post 권한 체크를 제외할 경로 설정
        if (request.getRequestURI().startsWith("/api/posts") || request.getRequestURI().startsWith("/api/notification")
        ||request.getRequestURI().startsWith("/ws") ||request.getRequestURI().startsWith("/WebSocketTest.html") || request.getRequestURI().startsWith("/sendMessage")) {
            return true; // 권한 검사 없이 통과
        }


        if (request.getRequestURI().startsWith("/admin")) {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(MemberRole.ADMIN.getValue()))) {
                return true;
                }
            } else {
                if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(MemberRole.USER.getValue()))) {
                    return true;
                }
            }
            response.setStatus(SC_FORBIDDEN);
            response.setCharacterEncoding("utf-8");
            new ObjectMapper().writeValue(response.getWriter(), "접근 권한이 없습니다.");
            return false;
        }
    }

