package com.project.Journey.board.service;


import com.project.Journey.board.dto.PostDTO;
import com.project.Journey.board.entity.Post;
import com.project.Journey.board.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public Long savePost(PostDTO postDTO) {


        Post post = Post.builder()
                .title(postDTO.getTitle())
                .content(postDTO.getContent())
                .destination(postDTO.getDestination())
                .start_date(postDTO.getStart_date())
                .end_date(postDTO.getEnd_date())
                .max_participants(postDTO.getMax_participants())
                .view_count(0)
                .comment_count(0)
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .user_id(postDTO.getUser_id())
                .build();

        return postRepository.save(post).getPost_id();
    }


    // 모든 게시글 조회
    public List<PostDTO> getAllPosts() {

        List<Post> postList = postRepository.findAll();
        List<PostDTO> postDtoList = new ArrayList<>();

        for(Post post : postList){
            PostDTO postDto = PostDTO.builder()
                    .title(post.getTitle())
                    .content(post.getContent())
                    .destination(post.getDestination())
                    .start_date(post.getStart_date())
                    .end_date(post.getEnd_date())
                    .max_participants(post.getMax_participants())
                    .view_count(post.getView_count())
                    .comment_count(post.getComment_count())
                    .created_at(post.getCreated_at())
                    .updated_at(post.getUpdated_at())
                    .user_id(post.getUser_id())
                    .build();
            postDtoList.add(postDto);
        }

        return postDtoList;
    }

    // post_id로 게시글 조회
    public PostDTO getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 post_id의 게시글이 없습니다"));

        return PostDTO.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .destination(post.getDestination())
                .start_date(post.getStart_date())
                .end_date(post.getEnd_date())
                .max_participants(post.getMax_participants())
                .view_count(post.getView_count())
                .comment_count(post.getComment_count())
                .created_at(post.getCreated_at())
                .updated_at(post.getUpdated_at())
                .user_id(post.getUser_id())
                .build();
    }
    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }
}
