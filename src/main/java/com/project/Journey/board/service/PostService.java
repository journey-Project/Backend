package com.project.Journey.board.service;


import com.project.Journey.awss3.S3Service;
import com.project.Journey.board.dto.*;
import com.project.Journey.board.entity.Post;
import com.project.Journey.board.entity.PostImage;
import com.project.Journey.board.exception.PostException;
import com.project.Journey.board.paging.PostSpecification;
import com.project.Journey.board.repository.PostImageRepository;
import com.project.Journey.board.repository.PostRepository;
import com.project.Journey.community.dto.CommunityMainHotPostDTO;
import com.project.Journey.board.paging.Pagination;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service

public class PostService {

    private final PostRepository postRepository;
    private final S3Service s3Service;
    private final PostImageRepository postImageRepository;
    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;


    public PostService(PostRepository postRepository, S3Service s3Service, PostImageRepository postImageRepository, @Qualifier("jsonRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.postRepository = postRepository;
        this.s3Service = s3Service;
        this.postImageRepository = postImageRepository;
        this.redisTemplate = redisTemplate;
    }

    private static final String VIEW_COUNT_KEY = "view_count:";

    //게시글 저장
    public Long savePost(PostRequestDTO postRequestDTO, MultipartFile coverImage, List<MultipartFile> images) {
        // 1️. S3에 커버 이미지 업로드 후 URL 저장
        String coverImageUrl = null;
        if (coverImage != null && !coverImage.isEmpty()) {
            coverImageUrl = s3Service.uploadApplicationImage(coverImage);
        }

        // 2. 게시글 객체 생성
        Post post = Post.builder()
                .user_id(postRequestDTO.getUserId())
                .country(postRequestDTO.getCountry())
                .title(postRequestDTO.getTitle())
                .startDate(postRequestDTO.getStartDate())
                .endDate(postRequestDTO.getEndDate())
                .max_participants(postRequestDTO.getParticipants())
                .destination(postRequestDTO.getDestination())
                .view_count(0)
                .comment_count(0)
                .coverImageUrl(coverImageUrl)
                .content(postRequestDTO.getContent())
                .createdAt(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .profileImageUrl("") //프로필 이미지
                .build();

        // 3️. 게시글을 먼저 저장 (DB에 저장)
        Post savedPost = postRepository.save(post);

        // 4️. 이미지 저장을 위한 리스트 생성
        List<PostImage> imageEntities = new ArrayList<>();

        // 5️. 이미지 업로드 및 PostImage 엔티티 생성
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                // S3에 업로드 후 URL 반환
                String imageUrl = s3Service.uploadApplicationImage(image);
                PostImage postImage = new PostImage(null, imageUrl, savedPost);
                imageEntities.add(postImage);
            }
        }

        // 6️. 이미지 엔티티 저장
        postImageRepository.saveAll(imageEntities);

        return savedPost.getPostId();
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
                    .start_date(post.getStartDate())
                    .end_date(post.getEndDate())
                    .max_participants(post.getMax_participants())
                    .view_count(post.getView_count())
                    .comment_count(post.getComment_count())
                    .created_at(post.getCreatedAt())
                    .updated_at(post.getUpdated_at())
                    .user_id(post.getUser_id())
                    .coverImageUrl(post.getCoverImageUrl())// image url
                    .country(post.getCountry()) //국가
                    .build();
            postDtoList.add(postDto);
        }

        return postDtoList;
    }

    //게시글 수정
    @Transactional
    public void updatePostById(Long postId,
                               PostDTO postDTO,
                               List<MultipartFile> newImages,
                               MultipartFile newCoverImage) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 post_id의 게시글이 없습니다"));

        // 기본 게시글 정보 업데이트
        post.updateTitle(postDTO.getTitle());
        post.updateContent(postDTO.getContent());
        post.updateDestination(postDTO.getDestination());
        post.updateMaxParticipants(postDTO.getMax_participants());
        post.updateStartDate(postDTO.getStart_date());
        post.updateEndDate(postDTO.getEnd_date());
        post.updateUpdateTime(postDTO.getUpdated_at());
        post.updateCountry(postDTO.getCountry());

        // ✅ [1] 기존 일반 이미지 처리
        List<PostImage> existingImages = postImageRepository.findByPost(post);
        List<String> remainingUrls = postDTO.getImageUrls();

        for (PostImage image : existingImages) {
            if (!remainingUrls.contains(image.getPostImageUrl())) {
                s3Service.deleteS3Image(image.getPostImageUrl());
                postImageRepository.delete(image);
            }
        }

        if (newImages != null) {
            for (MultipartFile file : newImages) {
                if (!file.isEmpty()) {
                    String imageUrl = s3Service.uploadApplicationImage(file);
                    //postImageRepository.save(new PostImage(post, imageUrl));
                    PostImage postImage = PostImage.builder()
                            .post(post)
                            .postImageUrl(imageUrl)
                            .build();
                    postImageRepository.save(postImage);
                }
            }
        }

        //2 커버 이미지 처리
        if (newCoverImage != null && !newCoverImage.isEmpty()) {
            // 기존 커버 이미지가 있다면 삭제
            if (post.getCoverImageUrl() != null && !post.getCoverImageUrl().isEmpty()) {
                s3Service.deleteS3Image(post.getCoverImageUrl());
            }

            // 새 커버 이미지 저장 및 URL 업데이트
            String newCoverImageUrl = s3Service.uploadApplicationImage(newCoverImage);
            post.updateCoverImageUrl(newCoverImageUrl);
        } else if (postDTO.getCoverImageUrl() != null && !postDTO.getCoverImageUrl().isEmpty()) {
            // 새 커버 이미지가 없지만, DTO에 기존 coverImageUrl이 있으면 유지
            post.updateCoverImageUrl(postDTO.getCoverImageUrl());
        }
    }






    /*
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
        post.updateCoverImageUrl(postDTO.getCoverImageUrl()); //커버 image url
        post.updateCountry(postDTO.getCountry());

    }
*/













    // post_id로 게시글 조회
    /*
    public PostDTO getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 post_id의 게시글이 없습니다"));

        return PostDTO.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .destination(post.getDestination())
                .start_date(post.getStartDate())
                .end_date(post.getEndDate())
                .max_participants(post.getMax_participants())
                .view_count(post.getView_count())
                .comment_count(post.getComment_count())
                .created_at(post.getCreated_at())
                .updated_at(post.getUpdated_at())
                .user_id(post.getUser_id())
                .coverImageUrl(post.getCoverImageUrl())
                .country(post.getCountry())
                .build();
    }

     */
    // 게시글 삭제
    /*
    @Transactional
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }
    */
    @Transactional
    public void deletePost(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException("해당 게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        //커버 이미지 삭제
        String coverImageUrl = post.getCoverImageUrl();
        if(coverImageUrl!= null && !coverImageUrl.isEmpty()){
            s3Service.deleteS3Image(coverImageUrl);
        }

        //첨부 이미지들 삭제
        List<PostImage> postImages = post.getImages();
        if(postImages != null && !postImages.isEmpty()){
            for(PostImage image : postImages){
                String imageUrl = image.getPostImageUrl();
                if(imageUrl != null && !imageUrl.isEmpty()){
                    s3Service.deleteS3Image(imageUrl);
                }
            }
        }

        //게시글 삭제
        postRepository.delete(post);
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

    //게시글 조회 - 이미지 불러오기 기능 추가
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

        List<PostImage> postImages = post.getImages();
        List<String> images = new ArrayList<>();
        for(PostImage postImage : postImages){
            images.add(postImage.getPostImageUrl());
            System.out.println(postImage.getPostImageUrl());
        }

        PostDTO postDTO = new PostDTO(
                post.getPostId(),
                post.getUser_id(),
                post.getTitle(),
                post.getContent(),
                post.getDestination(),
                post.getStartDate(),
                post.getEndDate(),
                post.getMax_participants(),
                post.getView_count(),
                post.getComment_count(),
                post.getCreatedAt(),
                post.getUpdated_at(),
                post.getCoverImageUrl(),
                post.getProfileImageUrl(),
                post.getCountry(),
                images
        );

       return postDTO;
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

    // user_id로 게시글 조회
    public List<PostDTO> getPostsByUserId(String user_id) {
        List<Post> list =  postRepository.findPostsByUserId(user_id);
        List<PostDTO> postDtoListByUserId = new ArrayList<>();

        for(Post post : list){
            PostDTO postDto = PostDTO.builder()
                    .title(post.getTitle())
                    .content(post.getContent())
                    .destination(post.getDestination())
                    .start_date(post.getStartDate())
                    .end_date(post.getEndDate())
                    .max_participants(post.getMax_participants())
                    .view_count(post.getView_count())
                    .comment_count(post.getComment_count())
                    .created_at(post.getCreatedAt())
                    .updated_at(post.getUpdated_at())
                    .user_id(post.getUser_id())
                    .coverImageUrl(post.getCoverImageUrl())
                    .country(post.getCountry())
                    .build();
            postDtoListByUserId.add(postDto);
        }
        return postDtoListByUserId;
    }


    //페이지네이션 적용
    public List<PostDTO> getPosts(int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        Page<Post> postPage = postRepository.findAll(pageable);
        //1.postPage에서 Post 객체들을 추출
        List<Post> posts = postPage.getContent();
        //2.Post 객체 리스트에서 PostResponseDto 객체 리스트로 변환
        List<PostDTO> postDTOList = new ArrayList<>();
        for(Post post: posts){
            PostDTO postDTO = PostDTO.builder()
                    .title(post.getTitle())
                    .content(post.getContent())
                    .destination(post.getDestination())
                    .start_date(post.getStartDate())
                    .end_date(post.getEndDate())
                    .max_participants(post.getMax_participants())
                    .view_count(post.getView_count())
                    .comment_count(post.getComment_count())
                    .created_at(post.getCreatedAt())
                    .updated_at(post.getUpdated_at())
                    .user_id(post.getUser_id())
                    .coverImageUrl(post.getCoverImageUrl())
                    .country(post.getCountry())
                    .build();
            postDTOList.add(postDTO);
        }
        return postDTOList;
    }

    public List<PostDTO> getPostsByCountry(String country, int page, int size){
        Page<Post> postPage = postRepository.findByCountry(country, PageRequest.of(page, size));

        List<PostDTO> postDTOList = new ArrayList<>();
        List<Post> posts = postPage.getContent();
        for(Post post: posts){
            PostDTO postDTO = PostDTO.builder()
                    .title(post.getTitle())
                    .content(post.getContent())
                    .destination(post.getDestination())
                    .start_date(post.getStartDate())
                    .end_date(post.getEndDate())
                    .max_participants(post.getMax_participants())
                    .view_count(post.getView_count())
                    .comment_count(post.getComment_count())
                    .created_at(post.getCreatedAt())
                    .updated_at(post.getUpdated_at())
                    .user_id(post.getUser_id())
                    .coverImageUrl(post.getCoverImageUrl())
                    .country(post.getCountry())
                    .build();
            postDTOList.add(postDTO);
        }
        return postDTOList;
    }



    public PostSearchResponseDTO getPostsByDateRangeAndCountry(LocalDate startDate, LocalDate endDate, String country, Pageable pageable) {
        Page<Post> posts = postRepository.findByStartDateBetweenAndCountry(startDate, endDate, country, pageable);

        // Entity → DTO 변환
        List<PostPageResponseDTO> content = posts.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PostSearchResponseDTO.builder()
                .content(content)
                .currentElements(content.size()) // 현재 페이지에서 가져온 게시글 개수
                .page(pageable.getPageNumber() + 1) // 1부터 시작하도록 변환
                .build();
    }

    private PostPageResponseDTO convertToDTO(Post post) {
        return PostPageResponseDTO.builder()
                .postId(post.getPostId())
                .destination(post.getDestination())
                .startDate(post.getStartDate())
                .endDate(post.getEndDate())
                .max_participants(post.getMax_participants())
                .title(post.getTitle())
                .coverImageUrl(post.getCoverImageUrl())
                .country(post.getCountry()) // 국가 정보 추가
                .build();
    }

    //동행자 모집 랜덤 게시글 가져오기
    // 랜덤 게시글 가져오기
    public List<PostPageResponseDTO> getRandomPosts(int count) {
        List<Post> posts = postRepository.findRandomPosts(count);

        // Entity → DTO 변환
        return posts.stream()
                .map(post -> PostPageResponseDTO.builder()
                        .postId(post.getPostId())
                        .destination(post.getDestination())
                        .startDate(post.getStartDate())
                        .endDate(post.getEndDate())
                        .max_participants(post.getMax_participants())
                        .title(post.getTitle())
                        .coverImageUrl(post.getCoverImageUrl())
                        .country(post.getCountry())
                        .build())
                .collect(Collectors.toList());
    }


    //동행자 모집 검색 & 페이지네이션
    public PostSearchResponse searchPosts(PostSearchRequest request) {

        // SearchDTO로부터 페이징 정보 추출
        int page = request.getPage(); // 1-based
        int size = request.getRecordSize(); // 한 페이지당 게시글 수
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // Specification 적용
        Specification<Post> spec = PostSpecification.search(request);
        Page<Post> postPage = postRepository.findAll(spec, pageable);

        // PostPageResponseDTO 리스트 생성
        List<PostPageResponseDTO> posts = postPage.getContent().stream()
                .map(post -> PostPageResponseDTO.builder()
                        .postId(post.getPostId())
                        .destination(post.getDestination())
                        .startDate(post.getStartDate())
                        .endDate(post.getEndDate())
                        .max_participants(post.getMax_participants())
                        .title(post.getTitle())
                        .coverImageUrl(post.getCoverImageUrl())
                        .country(post.getCountry())
                        .created_at(LocalDate.from(post.getCreatedAt()))
                        .build())
                .toList();

        // Pagination 객체 생성
        Pagination pagination = new Pagination((int) postPage.getTotalElements(), request);


        // 최종 응답 DTO 생성
        return new PostSearchResponse(posts, pagination);
    }


}
