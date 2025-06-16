package com.project.Journey.awss3;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.project.Journey.login.member.domain.MemberRole;

import java.util.List;

@Tag(
        name = "S3 파일업로드/다운로드",
        description = "AWS S3에 이미지를 업로드, 다운로드, 삭제하는 API 집합입니다."
)
@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    /**
     * 1) 프로필 이미지 업로드
     * 예) POST /api/s3/profile?role=USER
     *     form-data: file=[이미지]
     */

    @Operation(
            summary = "프로필 이미지 업로드",
            description = """
                프로필 이미지를 MemberRole에 해당하는 폴더에 업로드합니다.
                - 예) role=USER → S3 경로: `USER/{파일명}`
                - 업로드 후, 업로드된 이미지의 S3 URL을 반환합니다. (하단 예시 참고)
        
                [요청 예시]
                POST /api/s3/profile?role=USER
                Content-Type: multipart/form-data
                form-data: file=[이미지]
        
                [응답 예시]
                200 OK
                https://journeybucket0.s3.amazonaws.com/USER/{uuid}.jpg
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "업로드 성공 (이미지 URL 반환)",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name="성공 예시",
                                            value="https://journeybucket0.s3.amazonaws.com/USER/xxxxx.png"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "파일 형식 혹은 확장자가 잘못된 경우"
                    )
            }
    )
    @PostMapping("/profile")
    public ResponseEntity<String> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("role") MemberRole role
    ) {
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("5MB 이하의 파일만 업로드할 수 있습니다.");
        }

        String imageUrl = s3Service.uploadProfileImage(file, role);
        return ResponseEntity.ok(imageUrl);
    }

    /**
     * 2) 게시글 이미지 업로드
     * 예) POST /api/s3/application
     *     form-data: file=[이미지]
     */
    @Operation(
            summary = "게시글 이미지 업로드",
            description = """
        게시글(Application) 용 이미지를 `APPLICATION/` 폴더에 업로드합니다.
        - 업로드 후, 업로드된 이미지의 S3 URL을 반환합니다.
        
        [요청 예시]
        POST /api/s3/application
        Content-Type: multipart/form-data
        form-data: file=[이미지]
        
        [응답 예시]
        200 OK
        https://journeybucket0.s3.amazonaws.com/APPLICATION/{uuid}.jpg
    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "업로드 성공 (이미지 URL 반환)",
                            content = @Content(
                                    examples = @ExampleObject(
                                            name="성공 예시",
                                            value="https://journeybucket0.s3.amazonaws.com/APPLICATION/xxxxx.png"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "파일 형식 혹은 확장자가 잘못된 경우"
                    )
            }
    )
    @PostMapping("/application")
    public ResponseEntity<String> uploadApplicationImage(
            @RequestParam("file") MultipartFile file
    ) {
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("5MB 이하의 파일만 업로드할 수 있습니다.");
        }

        String imageUrl = s3Service.uploadApplicationImage(file);
        return ResponseEntity.ok(imageUrl);
    }

    /**
     * 3) 다운로드용 Pre-Signed URL 생성
     * 예) GET /api/s3/download?fileName=APPLICATION/uuid.jpg
     */
    @Operation(
            summary = "다운로드용 Pre-Signed URL 발급",
            description = """
                S3에 업로드된 파일을 다운로드할 수 있는 Pre-Signed URL을 생성합니다. 기본 3분 이내 유효합니다.
        
                [요청 예시]
                GET /api/s3/download?fileName=APPLICATION/xxxx.png
                (fileName은 S3 Key. 예: APPLICATION/{uuid}.png)
        
                [응답 예시]
                200 OK
                https://bucketjourney0.s3.amazonaws.com/APPLICATION/xxx.png?...&X-Amz-Signature=...
                
                위 URL로 GET 요청 시, 해당 이미지를 다운로드할 수 있습니다.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pre-signed URL 생성 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 fileName 또는 유효하지 않은 요청")
            }
    )
    @GetMapping("/download")
    public ResponseEntity<String> getPreSignedUrl(@RequestParam("fileName") String fileName) {
        String preSignedUrl = s3Service.getPreSignedUrlToDownload(fileName);
        return ResponseEntity.ok(preSignedUrl);
    }

    /**
     * 4) S3 이미지 삭제
     * 예) DELETE /api/s3?imageUrl=https://journeybucket0.s3.amazonaws.com/APPLICATION/xxxx.png
     */
    @Operation(
            summary = "S3 이미지 삭제",
            description = """
                이미 업로드된 S3 이미지(URL 기준)를 삭제합니다.
                
                [요청 예시]
                DELETE /api/s3?imageUrl=https://journeybucket0.s3.amazonaws.com/APPLICATION/xxxxx.png
                
                해당 URL에서 S3 Key를 추출하여 삭제 처리합니다.
                
                [응답 예시]
                204 No Content
            """,
            responses = {
                    @ApiResponse(responseCode = "204", description = "이미지 삭제 성공 (No Content)"),
                    @ApiResponse(responseCode = "400", description = "imageUrl 잘못되었거나 삭제 불가")
            }
    )
    @DeleteMapping
    public ResponseEntity<Void> deleteImage(@RequestParam("imageUrl") String imageUrl) {
        s3Service.deleteS3Image(imageUrl);
        return ResponseEntity.noContent().build();
    }
}
