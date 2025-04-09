//package com.project.Journey.login.jwt.filter;
//
//import com.auth0.jwt.exceptions.TokenExpiredException;
//import com.project.Journey.login.jwt.constants.JwtConstants;
//import com.project.Journey.login.jwt.constants.JwtUtils;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.io.PrintWriter;
//import java.time.Instant;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class JwtAuthenticationFilterTest {
//
//    private JwtAuthenticationFilter filter;
//    private HttpServletRequest request;
//    private HttpServletResponse response;
//    private FilterChain chain;
//    private PrintWriter writer;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        filter = new JwtAuthenticationFilter();
//        request = mock(HttpServletRequest.class);
//        response = mock(HttpServletResponse.class);
//        chain = mock(FilterChain.class);
//        writer = mock(PrintWriter.class);
//
//        when(response.getWriter()).thenReturn(writer);
//
//        SecurityContextHolder.clearContext();
//    }
//
////    @Test
////    @DisplayName("Whitelist 경로 요청 시 필터 수행 안함")
////    void shouldNotFilter_returnsTrueForWhitelist() {
////        // given
////        when(request.getRequestURI()).thenReturn("/login");
////
////        // when
////        boolean result = filter.shouldNotFilter(request);
////
////        // then
////        assertTrue(result);
////    }
//
//    @Test
//    @DisplayName("토큰 헤더가 없을 때 BadRequest 반환")
//    void doFilterInternal_noTokenHeader_returnsBadRequest() throws Exception {
//        // given
//        when(request.getRequestURI()).thenReturn("/notInWhitelist");
//        when(request.getHeader(JwtConstants.JWT_HEADER)).thenReturn(null);
//
//        // when
//        filter.doFilterInternal(request, response, chain);
//
//        // then
//        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
//        verify(chain, never()).doFilter(request, response);
//    }
//
//    @Test
//
//    @DisplayName("BEARER 타입이 없는 헤더일 때 BadRequest 반환")
//    void doFilterInternal_headerWithoutBearer_returnsBadRequest() throws Exception {
//        // given
//        when(request.getRequestURI()).thenReturn("/notInWhitelist");
//        when(request.getHeader(JwtConstants.JWT_HEADER)).thenReturn("SomethingInvalid");
//
//        // when
//        filter.doFilterInternal(request, response, chain);
//
//        // then
//        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
//        verify(chain, never()).doFilter(request, response);
//    }
//
//    @Test
//    @DisplayName("만료된 토큰일 때 Unauthorized 반환")
//    void doFilterInternal_expiredToken_returnsUnauthorized() throws Exception {
//        // given
//        when(request.getRequestURI()).thenReturn("/notInWhitelist");
//        String headerVal = JwtConstants.JWT_TYPE + "expiredTokenValue";
//        when(request.getHeader(JwtConstants.JWT_HEADER)).thenReturn(headerVal);
//
//        try (var mockedUtils = Mockito.mockStatic(JwtUtils.class)) {
//            mockedUtils.when(() -> JwtUtils.getTokenFromHeader(headerVal)).thenReturn("expiredTokenValue");
//            mockedUtils.when(() -> JwtUtils.verifyToken("expiredTokenValue"))
//                    .thenThrow(new TokenExpiredException("Token expired", Instant.now()));
//
//            // when
//            filter.doFilterInternal(request, response, chain);
//        }
//
//        // then
//        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        verify(chain, never()).doFilter(request, response);
//    }
//
//    @Test
//    @DisplayName("유효하지 않은 토큰일 때 BadRequest 반환")
//    void doFilterInternal_invalidToken_returnsBadRequest() throws Exception {
//        // given
//        when(request.getRequestURI()).thenReturn("/notInWhitelist");
//        String headerVal = JwtConstants.JWT_TYPE + "invalidTokenValue";
//        when(request.getHeader(JwtConstants.JWT_HEADER)).thenReturn(headerVal);
//
//        try (var mockedUtils = Mockito.mockStatic(JwtUtils.class)) {
//            mockedUtils.when(() -> JwtUtils.getTokenFromHeader(headerVal)).thenReturn("invalidTokenValue");
//            mockedUtils.when(() -> JwtUtils.verifyToken("invalidTokenValue"))
//                    .thenThrow(new RuntimeException("Invalid token"));
//
//            // when
//            filter.doFilterInternal(request, response, chain);
//        }
//
//        // then
//        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
//        verify(chain, never()).doFilter(request, response);
//    }
//
////    @Test
////    @DisplayName("유효한 토큰일 때 필터 체인 진행")
////    void testValidTokenFilterChainProgress() throws Exception {
////        // Mock HttpServletRequest
////        when(request.getRequestURI()).thenReturn("/secure");
////        when(request.getHeader(JwtConstants.JWT_HEADER)).thenReturn("Bearer validToken");
////
////        // Mock JwtUtils
////        try (var mockedUtils = Mockito.mockStatic(JwtUtils.class)) {
////            mockedUtils.when(() -> JwtUtils.getTokenFromHeader("Bearer validToken")).thenReturn("validToken");
////            mockedUtils.when(() -> JwtUtils.verifyToken("validToken")).thenReturn(mock(DecodedJWT.class));
////
////            // when
////            filter.doFilterInternal(request, response, chain);
////        }
////
////        // Verify the chain continues
////        verify(chain).doFilter(request, response);
////    }
//}
