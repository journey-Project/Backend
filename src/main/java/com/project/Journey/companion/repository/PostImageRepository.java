package com.project.Journey.companion.repository;


import com.project.Journey.companion.entity.Post;
import com.project.Journey.companion.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPost(Post post);

}
