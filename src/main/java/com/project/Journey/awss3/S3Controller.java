package com.project.Journey.awss3;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

//@Tag(name = "S3 파일업로드 API", description = """
//    AWS S3에 이미지(파일)를 업로드/삭제하는 API 집합입니다.<br/>
//    <ul>
//      <li><b>회원 사진</b>: member-photo</li>
//      <li><b>프로필 사진</b>: profile-img</li>
//      <li><b>게시판(단일)</b>: board-img</li>
//      <li><b>게시판(다중)</b>: board-images</li>
//      <li><b>파일삭제</b>: delete</li>
//    </ul>
//    """)
@Tag(name = "S3 관련 API(보완중/미구현)", description = "S3 관련 API(보완중/미구현)")
@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
@Slf4j
public class S3Controller {

    /**
     * FileService는 실제 업로드/삭제 로직이 구현된 Service 인터페이스/구현체입니다.
     * AwsFileService가 내부적으로 S3에 연결하여 이미지 업로드를 처리합니다.
     */
    private final FileService fileService;

    // ==========================================================
    // 1) 단일 회원 사진 업로드
    // ==========================================================
//    @Operation(
//            summary = "단일 회원 사진 업로드",
//            description = """
//            회원 사진(예: 갤러리, 일반 이미지)을 업로드합니다.<br/>
//            <b>memberId</b> 파라미터로 회원ID를 지정하고,<br/>
//            <b>MultipartFile</b>을 form-data로 전송하시면 됩니다.
//            """,
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "업로드 성공 - 업로드된 파일의 전체 URL 반환"),
//                    @ApiResponse(responseCode = "400", description = "업로드 실패 - 에러 메시지 반환")
//            }
//    )
    @PostMapping(
            value = "/member-photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadMemberPhoto(
            @Parameter(name = "file", description = "업로드할 이미지 파일", required = true)
            @RequestParam("file") MultipartFile file,

            @Parameter(name = "memberId", description = "회원 ID", in = ParameterIn.QUERY, required = true, example = "123")
            @RequestParam("memberId") Long memberId
    ) {
        try {
            String url = fileService.savePhoto(file, memberId);
            /**
             * [프론트엔드 응답 예시]
             * {
             *   "status": 200,
             *   "data": "https://{bucket}.s3.amazonaws.com/member/123/UUID.jpg"
             * }
             */
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            log.error("업로드 중 오류 발생", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==========================================================
    // 2) 프로필 사진 업로드
    // ==========================================================
//    @Operation(
//            summary = "프로필 사진 업로드",
//            description = """
//            프로필 사진을 업로드합니다.<br/>
//            <b>memberId</b>를 포함하여 form-data로 이미지를 전송합니다.
//            """,
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "업로드 성공 - 업로드된 파일 URL"),
//                    @ApiResponse(responseCode = "400", description = "업로드 실패 - 에러 메시지")
//            }
//    )
    @PostMapping(
            value = "/profile-img",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadProfileImg(
            @Parameter(name = "file", description = "업로드할 프로필 이미지 파일", required = true)
            @RequestParam("file") MultipartFile file,

            @Parameter(name = "memberId", description = "회원 ID", in = ParameterIn.QUERY, required = true, example = "123")
            @RequestParam("memberId") Long memberId
    ) {
        try {
            String url = fileService.saveProfileImg(file, memberId);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            log.error("업로드 중 오류 발생", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==========================================================
    // 3) 게시판 이미지(단일) 업로드
    // ==========================================================
//    @Operation(
//            summary = "게시판 이미지 업로드(단일)",
//            description = """
//            게시판에 첨부할 단일 이미지를 업로드합니다.<br/>
//            <b>boardId</b>에 해당하는 경로에 저장됩니다.
//            """,
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "업로드 성공 - 업로드된 파일 URL"),
//                    @ApiResponse(responseCode = "400", description = "업로드 실패 - 에러 메시지")
//            }
//    )
    @PostMapping(
            value = "/board-img",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadBoardImg(
            @Parameter(name = "file", description = "업로드할 이미지 파일(단일)", required = true)
            @RequestParam("file") MultipartFile file,

            @Parameter(name = "boardId", description = "게시판ID (또는 글ID)", in = ParameterIn.QUERY, required = true, example = "999")
            @RequestParam("boardId") Long boardId
    ) {
        try {
            String url = fileService.saveBoardImg(file, boardId);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            log.error("업로드 중 오류 발생", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==========================================================
    // 4) 게시판 이미지(다중) 업로드
    // ==========================================================
//    @Operation(
//            summary = "게시판 이미지 업로드(다중)",
//            description = """
//            게시판에 첨부할 여러 이미지를 업로드합니다.<br/>
//            <b>boardId</b>에 해당하는 경로에 여러 파일이 순차적으로 업로드됩니다.
//            """,
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "업로드 성공 - 업로드된 파일들의 URL 목록"),
//                    @ApiResponse(responseCode = "400", description = "업로드 실패 - 에러 메시지")
//            }
//    )
    @PostMapping(
            value = "/board-images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadBoardImages(
            @Parameter(name = "files", description = "업로드할 이미지 파일들(다중)", required = true)
            @RequestParam("files") List<MultipartFile> files,

            @Parameter(name = "boardId", description = "게시판ID (또는 글ID)", in = ParameterIn.QUERY, required = true, example = "999")
            @RequestParam("boardId") Long boardId
    ) {
        try {
            List<String> urls = fileService.saveBoardImages(files, boardId);
            /**
             * [프론트엔드 응답 예시]
             * {
             *   "status": 200,
             *   "data": [
             *       "https://{bucket}.s3.amazonaws.com/board/999/UUID1.jpg",
             *       "https://{bucket}.s3.amazonaws.com/board/999/UUID2.jpg"
             *   ]
             * }
             */
            return ResponseEntity.ok(urls);
        } catch (Exception e) {
            log.error("업로드 중 오류 발생", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==========================================================
    // 5) 파일 삭제
    // ==========================================================
//    @Operation(
//            summary = "파일 삭제",
//            description = """
//            파라미터로 전달된 fileKey를 S3에서 삭제합니다.<br/>
//            <b>fileKey</b>는 S3의 경로(키)입니다.
//            <br/>예) <code>profile/123/uuid.jpg</code>, <code>board/999/uuid.png</code>
//            """,
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "삭제 성공 - 메시지(삭제 완료)"),
//                    @ApiResponse(responseCode = "400", description = "삭제 실패 - 에러 메시지")
//            }
//    )
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(
            @Parameter(name = "fileKey", description = "S3에 저장된 파일 경로(키)", in = ParameterIn.QUERY, required = true,
                    example = "profile/123/uuid.jpg")
            @RequestParam("fileKey") String fileKey
    ) {
        try {
            fileService.deleteFile(fileKey);
            return ResponseEntity.ok("삭제 완료");
        } catch (Exception e) {
            log.error("삭제 중 오류 발생", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
