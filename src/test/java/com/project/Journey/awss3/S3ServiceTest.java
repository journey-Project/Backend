package com.project.Journey.awss3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.project.Journey.login.member.domain.MemberRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private S3Service s3Service;

    @Captor
    private ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3Service, "bucket", "journeybucket0");
    }

    @Test
    void testUploadProfileImage() throws IOException {
        // given
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("my-image.png");
        when(mockFile.getSize()).thenReturn(1234L);
        when(mockFile.getContentType()).thenReturn("image/png");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

        when(amazonS3.getUrl(anyString(), anyString()))
                .thenReturn(new java.net.URL("http://example.com/dummy.png"));

        // when
        String resultUrl = s3Service.uploadProfileImage(mockFile, MemberRole.USER);

        // then
        verify(amazonS3, times(1)).putObject(putObjectRequestCaptor.capture());
        PutObjectRequest capturedReq = putObjectRequestCaptor.getValue();

        // 🔍 업로드된 S3 키 출력 (디버깅)
        System.out.println("Captured S3 Key: " + capturedReq.getKey());

        // 검증
        assertTrue(capturedReq.getKey().startsWith("USER/"),
                "S3 Key should start with 'USER/', but was: " + capturedReq.getKey());

        assertEquals("image/png", capturedReq.getMetadata().getContentType());
        assertEquals("http://example.com/dummy.png", resultUrl);
    }


    @Test
    void testUploadApplicationImage() throws IOException {
        // given
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("doc.png");
        when(mockFile.getSize()).thenReturn(2000L);
        when(mockFile.getContentType()).thenReturn("application/png");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

        when(amazonS3.getUrl(anyString(), anyString()))
                .thenReturn(new java.net.URL("http://example.com/application/doc.png"));

        // when
        String resultUrl = s3Service.uploadApplicationImage(mockFile);

        // then
        verify(amazonS3).putObject(any(PutObjectRequest.class));
        assertEquals("http://example.com/application/doc.png", resultUrl);
    }

    @Test
    void testGetPreSignedUrlToDownload() {
        // given
        String fileName = "APPLICATION/test-file.png";
        String imageUrl = "https://journeybucket0.s3.amazonaws.com/" + fileName; // HTTPS 사용

        java.net.URL presigned = null;
        try {
            presigned = new java.net.URL("http://example.com/signed-url");
        } catch (Exception e) { /* ignore */ }

        when(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class)))
                .thenReturn(presigned);

        // when
        String resultUrl = s3Service.getPreSignedUrlToDownload(imageUrl);

        // then
        verify(amazonS3).generatePresignedUrl(any(GeneratePresignedUrlRequest.class));
        assertEquals("http://example.com/signed-url", resultUrl);
    }


    @Test
    void testDeleteS3Image() {
        // given
        String imageUrl = "https://journeybucket0.s3.amazonaws.com/APPLICATION/xyz.png";

        // when
        s3Service.deleteS3Image(imageUrl);

        // then
        verify(amazonS3).deleteObject("journeybucket0", "APPLICATION/xyz.png");
    }
}


