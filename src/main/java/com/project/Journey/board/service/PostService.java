package com.project.Journey.board.service;


import com.project.Journey.board.dto.PostDTO;
import com.project.Journey.board.entity.Post;
import com.project.Journey.board.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
//@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;


    public PostService(PostRepository postRepository, @Qualifier("jsonRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.postRepository = postRepository;
        this.redisTemplate = redisTemplate;
    }

    private static final String VIEW_COUNT_KEY = "view_count:";

    //게시글 저장
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
    };


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

    //게시글 수정
    @Transactional
    public void updatePostById(Long postId, PostDTO postDTO){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 post_id의 게시글이 없습니다"));
        post.updateTitle(postDTO.getTitle());
        post.updateContent(postDTO.getContent());
        post.updateDestination(postDTO.getDestination());
        post.updateMaxParticipants(postDTO.getMax_participants());
        post.updateStartDate(postDTO.getStart_date());
        post.updateEndDate(postDTO.getEnd_date());
        post.updateUpdateTime(postDTO.getUpdated_at());

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

    //조회 수가 높은 순서대로 조회(핫 게시글)
    public List<Post> getPostsByViewCount(){
        List<Post> hotPosts = postRepository.findAll();
        hotPosts.sort(new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                return o2.getView_count() - o1.getView_count();
            }
        });
        return hotPosts;
    }

    //게시글 조회 시 조회 수 증가
    @Transactional
    public PostDTO getPostByIdAndIncrementView(Long postId) {
        String redisKey = VIEW_COUNT_KEY + postId;

        // 1. Redis에서 조회수 증가
        Long updatedViewCount = redisTemplate.opsForValue().increment(redisKey, 1);

        // 2. Redis 키가 없거나 데이터가 초기화되지 않았을 경우 MySQL에서 초기화
        if (updatedViewCount == 1) { // Redis에 키가 없었던 경우
            Optional<Post> postOptional = postRepository.findById(postId);

            if (postOptional.isPresent()) {
                Post post = postOptional.get();
                redisTemplate.opsForValue().set(redisKey, post.getView_count()+1); // Redis 초기화 및 증가
                updatedViewCount = (long) (post.getView_count() + 1); // 증가된 값 업데이트
            } else {
                throw new IllegalArgumentException("해당 postId의 게시글이 없습니다.");
            }
        }

        // 4. 게시글 데이터 반환
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 postId의 게시글이 없습니다."));

        return PostDTO.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .destination(post.getDestination())
                .start_date(post.getStart_date())
                .end_date(post.getEnd_date())
                .max_participants(post.getMax_participants())
                .view_count(updatedViewCount.intValue()) // Redis 업데이트된 조회수 반환
                .comment_count(post.getComment_count())
                .created_at(post.getCreated_at())
                .updated_at(post.getUpdated_at())
                .user_id(post.getUser_id())
                .build();
    }


    //게시글 조회수 Redis와 MySQL 동기화 기능 구현
    @Transactional
    @Scheduled(fixedRate = 10000)
    public void syncViewCountToDatabase() {
        Set<String> keys = redisTemplate.keys(VIEW_COUNT_KEY + "*");

        if(keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            Long postId = Long.parseLong(key.replace(VIEW_COUNT_KEY, ""));

            try {
                //Redis에서 조회수 가져오기
                Integer viewCount = (Integer) redisTemplate.opsForValue().get(key); // JSON 문자열을 정수로 변환

                if (viewCount != null) {
                    //MySQL에 반영
                    postRepository.incrementViewCount(postId,viewCount); // MySQL 동기화
                    redisTemplate.delete(key); // Redis에서 데이터(키) 삭제
                }
            } catch (Exception e) {
                System.err.println("게시글 ID " + postId + "의 조회수를 동기화하는 중 오류가 발생했습니다. 키: " + key);
                e.printStackTrace();
            }
        }
    }





}
