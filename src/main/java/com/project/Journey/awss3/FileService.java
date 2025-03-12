//package com.project.Journey.awss3;
//
//import org.springframework.web.multipart.MultipartFile;
//import java.io.IOException;
//import java.util.List;
//
//public interface FileService {
//
//    /**
//     * 일반 이미지(사진) 업로드
//     * @param multipartFile 업로드할 파일
//     * @param memberId 어떤 회원(ID)과 연관된 업로드인지
//     * @return 업로드 후 S3에서 접근 가능한 URL
//     */
//    String savePhoto(MultipartFile multipartFile, Long memberId) throws IOException;
//
//    /**
//     * 프로필 이미지 업로드
//     * @param multipartFile 업로드할 파일
//     * @param memberId 어떤 회원(ID)과 연관된 업로드인지
//     * @return 업로드 후 S3에서 접근 가능한 URL
//     */
//    String saveProfileImg(MultipartFile multipartFile, Long memberId) throws IOException;
//
//    /**
//     * 게시판(혹은 글) 이미지 업로드 (단일)
//     */
//    String saveBoardImg(MultipartFile multipartFile, Long boardId) throws IOException;
//
//    /**
//     * 게시판(혹은 글) 이미지 업로드 (다중)
//     */
//    List<String> saveBoardImages(List<MultipartFile> files, Long boardId) throws IOException;
//
//    /**
//     * 파일 삭제 (선택 사항)
//     */
//    void deleteFile(String fileKey);
//}
