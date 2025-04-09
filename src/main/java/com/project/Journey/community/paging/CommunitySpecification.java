package com.project.Journey.community.paging;

import com.project.Journey.community.dto.SearchDTO;
import com.project.Journey.community.entity.Community;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public class CommunitySpecification {

    public static Specification<Community> searchByCriteria(SearchDTO searchDto) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchDto.getKeyword() != null && !searchDto.getKeyword().isEmpty()) {
                Predicate titlePredicate = cb.like(root.get("title"), "%" + searchDto.getKeyword() + "%");
                Predicate userPredicate = cb.like(root.get("user_id"), "%" + searchDto.getKeyword() + "%");
                predicates.add(cb.or(titlePredicate, userPredicate));
            }

            if (searchDto.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), searchDto.getStartDate().atStartOfDay()));
            }

            if (searchDto.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), searchDto.getEndDate().atTime(23, 59, 59)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
