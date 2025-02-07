package com.project.Journey.board.comment.controller;

import com.project.Journey.board.comment.domain.CommentDTO;
import com.project.Journey.board.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "댓글 API", description = "댓글 및 대댓글 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    // 댓글(대댓글) 생성
    @Operation(
            summary = "댓글 생성",
            description = "새로운 댓글 또는 대댓글을 생성합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "댓글 생성에 필요한 정보 (userId, content, postId 등)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "댓글 생성 예시",
                                    value = """
                    {
                      "userId": "testUser",
                      "content": "이 게시글 정말 유익하네요!",
                      "postId": 100,
                      "parentCommentId": null,
                      "depth": 0
                    }
                    """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "댓글 생성 성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name="성공 예시",
                                            value = """
                        {
                          "status": "success",
                          "message": "댓글이 생성되었습니다.",
                          "commentId": 123
                        }
                        """))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
                    @ApiResponse(responseCode = "404", description = "해당 Post나 부모 Comment를 찾지 못함")
            }
    )
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createComment(@Valid @RequestBody CommentDTO dto) {
        Long commentId = commentService.createComment(dto);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "댓글이 생성되었습니다.",
                "commentId", commentId
        ));
    }

    // 게시글 전체 댓글(대댓글 포함) 조회
    @Operation(
            summary = "게시글 전체 댓글 조회",
            description = "특정 게시글의 모든 댓글 및 대댓글 트리를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name="성공 예시",
                                            value = """
                        [
                          {
                            "commentId": 10,
                            "userId": "userA",
                            "content": "첫 댓글",
                            "createdAt": "2023-01-01T10:00:00",
                            "updatedAt": "2023-01-01T10:00:00",
                            "postId": 100,
                            "parentCommentId": null,
                            "depth": 0,
                            "childComments": [
                              {
                                "commentId": 11,
                                "userId": "userB",
                                "content": "대댓글",
                                "childComments": []
                              }
                            ]
                          }
                        ]
                        """
                                    )))
            }
    )
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDTO>> getAllComments(@PathVariable Long postId) {
        List<CommentDTO> commentList = commentService.getAllCommentsByPostId(postId);
        return ResponseEntity.ok(commentList);
    }

    // 댓글 수정
    @Operation(
            summary = "댓글 수정",
            description = "특정 댓글의 내용을 수정합니다. (userId 검증은 현재 미구현)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "수정할 댓글 내용",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name="수정 예시",
                                    value = """
                    {
                      "content": "수정된 댓글 내용"
                    }
                    """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "수정 성공"),
                    @ApiResponse(responseCode = "404", description = "해당 댓글이 없음")
            }
    )
    @PutMapping("/update/{commentId}")
    public ResponseEntity<Map<String, String>> updateComment(@PathVariable Long commentId, @Valid @RequestBody CommentDTO dto) {
        commentService.updateComment(commentId, dto.getContent());
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "댓글이 수정되었습니다."
        ));
    }

    // 댓글 삭제
    @Operation(
            summary = "댓글 삭제",
            description = "특정 댓글을 삭제합니다. 대댓글이 있으면 모두 함께 삭제됩니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "해당 댓글이 없음")
            }
    )
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "댓글이 삭제되었습니다."
        ));
    }
}