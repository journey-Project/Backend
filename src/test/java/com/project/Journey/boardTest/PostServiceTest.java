//package com.project.Journey.boardTest;
//
//import com.project.Journey.board.dto.PostDTO;
//import com.project.Journey.board.entity.Post;
//import com.project.Journey.board.repository.PostRepository;
//import com.project.Journey.board.service.PostService;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@SpringBootTest
//public class PostServiceTest {
//
//    @Autowired
//    private PostService postService;
//
//    @Autowired
//    private PostRepository postRepository;
//
//
//    @Test
//    @DisplayName("게시글 작성")
//    void savePostTest(){
//        //given - 게시글 저장 요청
//        PostDTO postDTO = PostDTO.builder()
//                .title("test title")
//                .content("test content")
//                .destination("test destination")
//                .start_date(LocalDate.now())
//                .end_date(LocalDate.now().plusDays(1))
//                .max_participants(10)
//                .view_count(0)
//                .comment_count(0)
//                .created_at(LocalDateTime.now())
//                .updated_at(LocalDateTime.now())
//                .user_id("test user")
//                .build();
//
//        //when
//        Long saved_id = postService.savePost(postDTO);
//
//        //then
//        Post post = postRepository.findById(saved_id)
//                .orElseThrow(() -> new IllegalArgumentException("해당 post_id의 게시글이 없습니다"));
//
//        Assertions.assertEquals(saved_id, post.getPostId());
//        Assertions.assertEquals("test title",post.getTitle());
//        Assertions.assertEquals("test content", post.getContent());
//        Assertions.assertEquals("test user", post.getUser_id());
//    }
//
//
//
//
//}
