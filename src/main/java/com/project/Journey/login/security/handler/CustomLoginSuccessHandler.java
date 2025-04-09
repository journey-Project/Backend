//package com.project.Journey.login.security.handler;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//import static jakarta.servlet.http.HttpServletResponse.SC_OK;
//import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
//
//@Slf4j
//public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
//
//    @Override
//    public void onAuthenticationSuccess(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            Authentication authentication
//    ) throws IOException, ServletException {
//
//        // 예시) JSON 응답
//        response.setStatus(SC_OK);
//        response.setContentType(APPLICATION_JSON_VALUE);
//        response.setCharacterEncoding("utf-8");
//
//        var successBody = new SuccessResponse("로그인 성공", authentication.getName());
//        new ObjectMapper().writeValue(response.getWriter(), successBody);
//
//        // super.onAuthenticationSuccess(request, response, authentication);
//        // ↑ 호출 시, Security가 "저장된 요청(saved request)"이 있으면 해당 URL로 리다이렉트
//        // JSON만 내려주고 싶으면 호출 X
//    }
//
//    static class SuccessResponse {
//        private final String message;
//        private final String username;
//
//        public SuccessResponse(String message, String username) {
//            this.message = message;
//            this.username = username;
//        }
//
//        public String getMessage() {
//            return message;
//        }
//
//        public String getUsername() {
//            return username;
//        }
//    }
//}
