//package com.project.Journey.login.security.filter;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.project.Journey.login.member.domain.Member;
//import com.project.Journey.login.security.exception.InputNotFoundException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import java.io.IOException;
//
//public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//
//    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
//        super(authenticationManager);
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//        UsernamePasswordAuthenticationToken authRequest;
//
//        try {
//            Member member = new ObjectMapper().readValue(request.getInputStream(), Member.class);
//            authRequest = new UsernamePasswordAuthenticationToken(member.getLoginId(), member.getPassword());
//        } catch (IOException e) {
//            throw new InputNotFoundException("입력이 올바르지 않습니다");
//        }
//
//        setDetails(request, authRequest);
//        return this.getAuthenticationManager().authenticate(authRequest);
//    }
//
//}
