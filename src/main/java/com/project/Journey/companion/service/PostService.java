package com.project.Journey.companion.service;


import com.project.Journey.awss3.S3Service;
import com.project.Journey.companion.dto.*;
import com.project.Journey.companion.entity.Post;
import com.project.Journey.companion.entity.PostImage;
import com.project.Journey.companion.exception.PostException;
import com.project.Journey.companion.paging.PostSpecification;
import com.project.Journey.companion.repository.PostImageRepository;
import com.project.Journey.companion.repository.PostRepository;
import com.project.Journey.companion.paging.Pagination;
import com.project.Journey.login.member.domain.Member;
import com.project.Journey.login.member.repository.MemberRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    private final MemberRepository memberRepository;


    public PostService(PostRepository postRepository, S3Service s3Service, PostImageRepository postImageRepository, @Qualifier("jsonRedisTemplate") RedisTemplate<String, Object> redisTemplate,
		MemberRepository memberRepository) {
        this.postRepository = postRepository;
        this.s3Service = s3Service;
        this.postImageRepository = postImageRepository;
        this.redisTemplate = redisTemplate;
		this.memberRepository = memberRepository;
	}

    private static final String VIEW_COUNT_KEY = "view_count:";

    //게시글 저장
    public Long savePost(PostRequestDTO dto,
                         MultipartFile coverImage,
                         List<MultipartFile> images) {

        // ① 작성자 조회
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // ② 커버 이미지 S3 업로드
        String coverUrl = (coverImage != null && !coverImage.isEmpty())
                ? s3Service.uploadApplicationImage(coverImage)
                : null;

        // ③ Post 엔티티 생성
        Post post = Post.builder()
                .member(member)
                .country(dto.getCountry())
                .title(dto.getTitle())
                .content(dto.getContent())
                .destination(dto.getDestination())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .max_participants(dto.getParticipants())
                .coverImageUrl(coverUrl)
                .view_count(0)
                .comment_count(0)
                .createdAt(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        Post saved = postRepository.save(post);

        // ④ 첨부 이미지 처리 (선택)
        if (images != null && !images.isEmpty()) {
            List<PostImage> list = images.stream()
                    .filter(mf -> !mf.isEmpty())
                    .map(mf -> {
                        String url = s3Service.uploadApplicationImage(mf);
                        return PostImage.builder()
                                .post(saved)
                                .postImageUrl(url)
                                .build();
                    }).toList();
            postImageRepository.saveAll(list);
        }
        return saved.getPostId();
    }


    // 모든 게시글 조회
    public List<PostResponseDTO> getAllPosts(Long currentUserId) {
        return postRepository.findAll().stream()
                .map(p -> toDto(p, currentUserId))      // ★ isMine 적용
                .toList();
    }
    //게시글 수정
    @Transactional
    public void updatePostById(Long postId,
                               PostResponseDTO postResponseDTO,
                               List<MultipartFile> newImages,
                               MultipartFile newCoverImage) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 post_id의 게시글이 없습니다"));

        // 기본 게시글 정보 업데이트
        post.updateTitle(postResponseDTO.getTitle());
        post.updateContent(postResponseDTO.getContent());
        post.updateDestination(postResponseDTO.getDestination());
        post.updateMaxParticipants(postResponseDTO.getMax_participants());
        post.updateStartDate(postResponseDTO.getStart_date());
        post.updateEndDate(postResponseDTO.getEnd_date());
        post.updateUpdateTime(postResponseDTO.getUpdated_at());
        post.updateCountry(postResponseDTO.getCountry());

        // ✅ [1] 기존 일반 이미지 처리
        List<PostImage> existingImages = postImageRepository.findByPost(post);
        List<String> remainingUrls = postResponseDTO.getImageUrls();

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
            // 새 커버 이미지가 올라온 경우 → 기존 이미지 삭제 후 새 이미지 등록
            if (post.getCoverImageUrl() != null && !post.getCoverImageUrl().isEmpty()) {
                s3Service.deleteS3Image(post.getCoverImageUrl());
            }

            // 새 커버 이미지 저장 및 URL 업데이트
            String newCoverImageUrl = s3Service.uploadApplicationImage(newCoverImage);
            post.updateCoverImageUrl(newCoverImageUrl);
        } else if (postResponseDTO.getCoverImageUrl() != null && !postResponseDTO.getCoverImageUrl().isEmpty()) {
            // 새 커버 이미지가 없지만, DTO에 기존 coverImageUrl이 있으면 유지
            post.updateCoverImageUrl(postResponseDTO.getCoverImageUrl());
        } else{
            // 커버 이미지를 삭제하고 싶은 경우(커버이미지가 없게)
            if(post.getCoverImageUrl() != null && !post.getCoverImageUrl().isEmpty()){
                s3Service.deleteS3Image(post.getCoverImageUrl());
                post.updateCoverImageUrl(null); //DB에서도 NULL 처리
            }
        }
    }


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
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException("게시글이 존재하지 않습니다", HttpStatus.NOT_FOUND));

        // 커버 + 첨부 이미지 S3 삭제
        Optional.ofNullable(post.getCoverImageUrl()).ifPresent(s3Service::deleteS3Image);
        post.getImages().forEach(pi -> s3Service.deleteS3Image(pi.getPostImageUrl()));

        postRepository.delete(post);
    }

    //조회 수가 높은 순서대로 조회(핫 게시글)
    @Transactional
    public PostResponseDTO getPostByIdAndIncrementView(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        post.setView_count(post.getView_count() + 1);  // 바로 증가
        // save 생략해도 트랜잭션 끝나면 dirty checking 으로 반영됨

        return toDto(post, currentUserId);  // isMine 포함된 DTO 반환
    }




    //게시글 조회수 Redis와 MySQL 동기화 기능 구현
    @Scheduled(fixedRate = 10_000)
    public void syncViewCountToDatabase() {
        Set<String> keys = redisTemplate.keys(VIEW_COUNT_KEY + "*");
        if (keys == null) return;

        for (String key : keys) {
            Long postId = Long.parseLong(key.replace(VIEW_COUNT_KEY, ""));
            Integer views = (Integer) redisTemplate.opsForValue().get(key);
            if (views != null) {
                postRepository.incrementViewCount(postId, views);
                redisTemplate.delete(key);
            }
        }
    }

    // user_id로 게시글 조회
    public List<PostResponseDTO> getPostsByMemberId(Long memberId) {
        List<Post> list =  postRepository.findPostsByMemberId(memberId);
        List<PostResponseDTO> postResponseDtoListByMemberId = new ArrayList<>();

        for(Post post : list){
            PostResponseDTO postResponseDto = PostResponseDTO.builder()
                    .postId(post.getPostId())
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
                    .nickname(post.getMember().getNickname())
                    .profileImageUrl(post.getMember().getProfileImage())
                    .coverImageUrl(post.getCoverImageUrl())
                    .country(post.getCountry())
                    .build();
            postResponseDtoListByMemberId.add(postResponseDto);
        }
        return postResponseDtoListByMemberId;
    }


    //페이지네이션 적용
    public List<PostResponseDTO> getPosts(int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        Page<Post> postPage = postRepository.findAll(pageable);
        //1.postPage에서 Post 객체들을 추출
        List<Post> posts = postPage.getContent();
        //2.Post 객체 리스트에서 PostResponseDto 객체 리스트로 변환
        List<PostResponseDTO> postResponseDTOList = new ArrayList<>();
        for(Post post: posts){
            PostResponseDTO postResponseDTO = PostResponseDTO.builder()
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
                    .nickname(post.getMember().getNickname())
                    .profileImageUrl(post.getMember().getProfileImage())
                    .coverImageUrl(post.getCoverImageUrl())
                    .country(post.getCountry())
                    .build();
            postResponseDTOList.add(postResponseDTO);
        }
        return postResponseDTOList;
    }

    public List<PostResponseDTO> getPostsByCountry(String country, int page, int size){
        Page<Post> postPage = postRepository.findByCountry(country, PageRequest.of(page, size));

        List<PostResponseDTO> postResponseDTOList = new ArrayList<>();
        List<Post> posts = postPage.getContent();
        for(Post post: posts){
            PostResponseDTO postResponseDTO = PostResponseDTO.builder()
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
                    .nickname(post.getMember().getNickname())
                    .profileImageUrl(post.getMember().getProfileImage())
                    .coverImageUrl(post.getCoverImageUrl())
                    .country(post.getCountry())
                    .build();
            postResponseDTOList.add(postResponseDTO);
        }
        return postResponseDTOList;
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


    public List<PostResponseDTO> getPostsByViewCount() {
        return getPostsByViewCount(null);           // 기본값: 로그인 안 함
    }

    public List<PostResponseDTO> getPostsByViewCount(Long currentUserId) { // ★ 새로 추가
        List<Post> hot = postRepository.findAll(Sort.by(Sort.Direction.DESC, "view_count"));
        return hot.stream()
                .map(p -> toDto(p, currentUserId))
                .toList();
    }

    private PostResponseDTO toDto(Post p, Long currentUserId) {
        boolean mine = currentUserId != null &&
                p.getMember().getId().equals(currentUserId);

        return PostResponseDTO.builder()
                .postId(p.getPostId())
                .writerId(p.getMember().getId())            // 작성자 PK
                .isMine(mine)                               // 로그인한 사용자가 작성자인지
                .nickname(p.getMember().getNickname())      // 작성자 닉네임
                .title(p.getTitle())                        // 제목
                .content(p.getContent())                    // 본문 내용
                .destination(p.getDestination())            // 여행지
                .start_date(p.getStartDate())               // 여행 시작일
                .end_date(p.getEndDate())                   // 여행 종료일
                .max_participants(p.getMax_participants())  // 최대 인원
                .view_count(p.getView_count())              // 조회수
                .comment_count(p.getComment_count())        // 댓글 수
                .created_at(p.getCreatedAt())               // 작성일
                .updated_at(p.getUpdated_at())              // 수정일
                .coverImageUrl(p.getCoverImageUrl())        // 커버 이미지
                .profileImageUrl(p.getMember().getProfileImage()) // 작성자 프로필 이미지
                .country(p.getCountry())                    // 국가
                .imageUrls(p.getImages().stream()           // 이미지 리스트
                        .map(PostImage::getPostImageUrl)
                        .toList())
                .build();
    }

}
