package com.project.Journey.awss3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AwsFileService implements FileService {

    private final AmazonS3Client amazonS3Client;

    /**
     * application.yml (또는 .properties)에 다음과 같이 설정한다 가정:
     * cloud.aws.s3.bucket=journeybucket0
     */
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 필요하다면, S3에 저장할 폴더명을 상수로 관리
    private static final String PROFILE_IMG_DIR = "profile/";
    private static final String MEMBER_IMG_DIR = "member/";
    private static final String BOARD_IMG_DIR = "board/"; // 게시판 이미지용

    /**
     * 일반 사진 업로드 (Member 사진이라 가정)
     */
    @Override
    public String savePhoto(MultipartFile multipartFile, Long memberId) throws IOException {
        // member/1234/uuid.jpg  형태로 저장
        return uploadToS3(multipartFile, MEMBER_IMG_DIR, memberId);
    }

    /**
     * 프로필 사진 업로드
     */
    @Override
    public String saveProfileImg(MultipartFile multipartFile, Long memberId) throws IOException {
        // profile/1234/uuid.jpg 형태로 저장
        return uploadToS3(multipartFile, PROFILE_IMG_DIR, memberId);
    }

    /**
     * 게시판 이미지(단일) 업로드
     */
    @Override
    public String saveBoardImg(MultipartFile multipartFile, Long boardId) throws IOException {
        // board/1234/uuid.jpg 형태로 저장
        return uploadToS3(multipartFile, BOARD_IMG_DIR, boardId);
    }

    /**
     * 게시판 이미지(다중) 업로드
     */
    @Override
    public List<String> saveBoardImages(List<MultipartFile> files, Long boardId) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            String url = uploadToS3(file, BOARD_IMG_DIR, boardId);
            imageUrls.add(url);
        }
        return imageUrls;
    }

    /**
     * S3에 파일 업로드 (공통 로직)
     */
    private String uploadToS3(MultipartFile multipartFile, String dirName, Long id) throws IOException {
        // 1) 확장자 추출
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = "";
        if (originalFilename != null) {
            int idx = originalFilename.lastIndexOf(".");
            if (idx != -1) {
                extension = originalFilename.substring(idx); // ".png", ".jpg" 등
            }
        }

        // 2) S3 Key 생성 (dir + id + "/" + UUID + 확장자)
        String fileName = dirName + id + "/" + UUID.randomUUID() + extension;

        // 3) 메타데이터
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        // 4) 업로드
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, multipartFile.getInputStream(), metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );

        // 5) 업로드된 파일의 URL 반환
        String uploadImageUrl = amazonS3Client.getUrl(bucket, fileName).toString();
        log.info("File uploaded to S3. bucket={}, key={}, url={}", bucket, fileName, uploadImageUrl);
        return uploadImageUrl;
    }

    /**
     * S3 파일 삭제
     */
    @Override
    public void deleteFile(String fileKey) {
        log.info("Delete file from S3. bucket={}, key={}", bucket, fileKey);
        amazonS3Client.deleteObject(bucket, fileKey);
    }

    /**
     * 필요하다면 폴더(디렉토리) 생성
     * (S3는 실제 디렉토리 개념이 아니므로, 이런 식으로 빈 객체를 생성해서 폴더 구조 흉내냄)
     */
    public void createDir(String folderName) {
        log.info("Create directory in S3: {}/{}", bucket, folderName);
        amazonS3Client.putObject(
                bucket,
                folderName.endsWith("/") ? folderName : (folderName + "/"),
                new ByteArrayInputStream(new byte[0]),
                new ObjectMetadata()
        );
    }
}
