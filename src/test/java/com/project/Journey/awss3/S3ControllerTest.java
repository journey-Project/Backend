//package com.project.Journey.awss3;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.BDDMockito;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.Arrays;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.BDDMockito.given;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart; // multipart import
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(SpringExtension.class)
//@WebMvcTest(S3Controller.class)
//class S3ControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private FileService fileService; // 실제 구현체 대신 모킹
//
//    @Test
//    @DisplayName("회원 사진 업로드 (단일)")
//    void testUploadMemberPhoto() throws Exception {
//        // given
//        // Service mock
//        given(fileService.savePhoto(any(MultipartFile.class), eq(123L)))
//                .willReturn("https://fake-s3-url.com/member/123/uuid.png");
//
//        // when & then
//        mockMvc.perform(
//                        multipart("/api/s3/member-photo?memberId=123")
//                                .file("file", "fake-image-content".getBytes())
//                                .contentType(MediaType.MULTIPART_FORM_DATA)
//                )
//                .andExpect(status().isOk())
//                .andExpect(content().string("https://fake-s3-url.com/member/123/uuid.png"));
//    }
//
//    @Test
//    @DisplayName("게시판 이미지 업로드 (다중)")
//    void testUploadBoardImages() throws Exception {
//        // given
//        Long boardId = 999L;
//        given(fileService.saveBoardImages(any(), eq(boardId)))
//                .willReturn(Arrays.asList(
//                        "https://fake-s3.com/board/999/img1.jpg",
//                        "https://fake-s3.com/board/999/img2.jpg"
//                ));
//
//        // when & then
//        mockMvc.perform(
//                        multipart("/api/s3/board-images?boardId=999")
//                                .file("files", "content-of-file1".getBytes())
//                                .file("files", "content-of-file2".getBytes())
//                                .contentType(MediaType.MULTIPART_FORM_DATA)
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0]").value("https://fake-s3.com/board/999/img1.jpg"))
//                .andExpect(jsonPath("$[1]").value("https://fake-s3.com/board/999/img2.jpg"));
//    }
//}
