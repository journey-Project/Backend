/*
package com.project.Journey.Community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Spring Security 필터 비활성화
public class CommunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreatePost() throws Exception {
        // 요청 JSON 데이터 생성
        String jsonRequest = """
            {
                "user_id": "테스트유저",
                "country": "국내",
                "title": "제목 입력 테스트",
                "content": "내용 입력 테스트",
                "imageUrls": []
            }
        """;

        // JSON 데이터를 MultipartFile 형식으로 변환
        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                "application/json",
                jsonRequest.getBytes(StandardCharsets.UTF_8)
        );

        // 테스트용 이미지 파일 생성
        MockMultipartFile image1 = new MockMultipartFile(
                "images",
                "test_image.jpg",
                "image/jpg",
                new byte[100]
        );

        MockMultipartFile image2 = new MockMultipartFile(
                "images",
                "test_image2.jpg",
                "image/jpg",
                new byte[100]
        );

        // API 요청 실행 및 검증
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/community/save")
                        .file(dataPart)   // JSON 데이터 추가
                        .file(image1)     // 이미지 1 추가
                        .file(image2)     // 이미지 2 추가
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())  // 응답 상태 검증
                .andDo(print());  // 결과 출력
    }
}
*/