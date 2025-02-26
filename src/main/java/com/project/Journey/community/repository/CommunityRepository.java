package com.project.Journey.community.repository;

import com.project.Journey.community.entity.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    Page<Community> findByCountry(String country, Pageable pageable);
    Optional<Community> findById(Long id);

}
