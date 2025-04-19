package com.project.Journey.community.paging;

import com.project.Journey.community.dto.SearchDTO;
import com.project.Journey.community.entity.Community;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public class CommunitySpecification {

    public static Specification<Community> searchWithFilters(SearchDTO dto) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (dto.getCommunityPostId() != null) {
                predicates.add(cb.equal(root.get("community_post_id"), dto.getCommunityPostId()));
            }

            if (dto.getTitle() != null && !dto.getTitle().isEmpty()) {
                predicates.add(cb.like(root.get("title"), "%" + dto.getTitle() + "%"));
            }

            if (dto.getUserId() != null && !dto.getUserId().isEmpty()) {
                predicates.add(cb.equal(root.get("user_id"), dto.getUserId()));
            }

            if (dto.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), dto.getStartDate().atStartOfDay()));
            }

            if (dto.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), dto.getEndDate().atTime(23, 59, 59)));
            }

            if (dto.getCountry() != null && !dto.getCountry().isEmpty()) {
                predicates.add(cb.equal(root.get("country"), dto.getCountry()));
            }

            query.orderBy(cb.desc(root.get("createdAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
