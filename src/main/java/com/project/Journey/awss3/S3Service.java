package com.project.Journey.awss3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;

import com.project.Journey.login.member.domain.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.amazonaws.HttpMethod.GET;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final static String APPLICATION_PATH = "APPLICATION"; // 게시글 관련 이미지 저장 경로
    private final static String MODEL_PROFILE_PATH = "HAIR_MODEL_PROFILE"; // 프로필 이미지 저장 경로
    private final static String MODEL_PROFILE_IMAGE_NAME = "/model_default_profile.png"; // 기본 프로필 이미지 파일명
    private final static int IMAGE_URL_PREFIX_LENGTH = 41; // S3 URL에서 버킷명을 제외한 이미지 키 추출을 위한 길이

    private final static int EXPIRED_TIME = 3; // Pre-signed URL 만료 시간 (단위: 분)
    private final AmazonS3 amazonS3;

    @Value("${AWS_S3_BUCKET}")
    private String bucket;

    /**
     * 사용자 프로필 이미지를 S3에 업로드하는 메서드
     * @param multipartFile 업로드할 이미지 파일
     * @param role 사용자의 역할 (MemberRole)
     * @return 업로드된 이미지의 S3 URL
     */
    public String uploadProfileImage(MultipartFile multipartFile, MemberRole role) {
        return uploadImage(multipartFile, role.name());
    }

    /**
     * 게시글(Application) 이미지 업로드 메서드
     * @param multipartFile 업로드할 이미지 파일
     * @return 업로드된 이미지의 S3 URL
     */
    public String uploadApplicationImage(MultipartFile multipartFile) {
        return uploadImage(multipartFile, APPLICATION_PATH);
    }


    /**
     * 기본 프로필 이미지의 URL을 반환하는 메서드
     * @return 기본 프로필 이미지의 S3 URL
     */
    public String getDefaultProfileImageUrl() {
        return amazonS3.getUrl(bucket, MODEL_PROFILE_PATH + MODEL_PROFILE_IMAGE_NAME).toString();
    }


    /**
     * 특정 이미지 파일을 다운로드할 수 있는 Pre-signed URL을 생성하는 메서드
     * @param fileName S3에 저장된 파일 이름
     * @return 일정 시간 동안 유효한 다운로드 URL
     */
    public String getPreSignedUrlToDownload(final String fileName) {
        final String imageKey = getImageUrlToKey(fileName);

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, imageKey)
                .withMethod(GET)
                .withExpiration(getExpiredTime())
                .withResponseHeaders(new ResponseHeaderOverrides().withContentDisposition("attachment"));

        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }

    /**
     * S3에 저장된 이미지를 삭제하는 메서드
     * @param imageUrl 삭제할 이미지의 S3 URL
     */
    public void deleteS3Image(final String imageUrl) {
        final String imageKey = getImageUrlToKey(imageUrl);
        amazonS3.deleteObject(bucket, imageKey);
    }

    /**
     * S3에 이미지를 업로드하는 공통 메서드
     * @param multipartFile 업로드할 이미지 파일
     * @param path 저장될 디렉토리 경로
     * @return 업로드된 이미지의 S3 URL
     */
    private String uploadImage(MultipartFile multipartFile, String path) {
        String fileName = createFileName(multipartFile.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

//        try (InputStream inputStream = multipartFile.getInputStream()) {
//            amazonS3.putObject(new PutObjectRequest(bucket + "/" + path, fileName, inputStream, objectMetadata));
//            return amazonS3.getUrl(bucket + "/" + path, fileName).toString();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        // Key = path + "/" + fileName
        String key = path + "/" + fileName;

        try (InputStream inputStream = multipartFile.getInputStream()) {
            // 1) putObject(버킷이름, 키, ...)
            amazonS3.putObject(new PutObjectRequest(bucket, key, inputStream, objectMetadata));

            // 2) 업로드 후 접근 가능한 S3 URL 반환
            return amazonS3.getUrl(bucket, key).toString();

        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }
    }

    /**
     * S3 URL에서 파일의 Key 값만 추출하는 메서드
     * @param imageUrl S3 URL
     * @return S3 Key 값 (ex: "APPLICATION/uuid.jpg")
     */
    private String getImageUrlToKey(final String imageUrl) {
        // 정확한 버킷 URL을 동적으로 생성
        String bucketUrl = "https://" + bucket + ".s3.amazonaws.com/";

        // 이미지 URL이 버킷 URL로 시작하는지 확인 후, Key만 추출
        if (imageUrl.startsWith(bucketUrl)) {
            return imageUrl.substring(bucketUrl.length()); // 정확한 key 추출
        }

        // URL이 예상한 형식이 아니면 예외 발생
        throw new IllegalArgumentException("Invalid S3 URL: " + imageUrl);
    }

    /**
     * Pre-signed URL의 만료 시간을 설정하는 메서드 (현재 3분)
     * @return 만료 시간 설정된 Date 객체
     */
    private Date getExpiredTime() {
        Date expiration = new Date();
        long expTime = expiration.getTime();
        expTime += TimeUnit.MINUTES.toMillis(EXPIRED_TIME);
        expiration.setTime(expTime);
        return expiration;
    }

    /**
     * 랜덤 UUID를 기반으로 고유한 파일명을 생성하는 메서드
     * @param fileName 원본 파일명
     * @return UUID가 적용된 새로운 파일명
     */
    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    /**
     * 파일 확장자를 추출하고, 허용된 확장자인지 검증하는 메서드
     * @param fileName 원본 파일명
     * @return 파일 확장자 (.jpg, .png 등)
     */
    private String getFileExtension(String fileName) {
        if (fileName.isEmpty()) throw new IllegalArgumentException();

        // 허용할 파일 확장자 목록
        ArrayList<String> fileValidate = new ArrayList<>();
        fileValidate.add(".jpg");
        fileValidate.add(".jpeg");
        fileValidate.add(".png");
        fileValidate.add(".JPG");
        fileValidate.add(".JPEG");
        fileValidate.add(".PNG");
        fileValidate.add(".HEIC");
        fileValidate.add(".heic");

        // 파일 확장자 추출
        String idxFileName = fileName.substring(fileName.lastIndexOf("."));
        if (!fileValidate.contains(idxFileName)) throw new IllegalArgumentException();

        return fileName.substring(fileName.lastIndexOf("."));
    }
}
