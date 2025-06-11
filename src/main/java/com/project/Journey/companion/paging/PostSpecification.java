package com.project.Journey.companion.paging;

import com.project.Journey.companion.dto.PostSearchRequest;
import com.project.Journey.companion.entity.Post;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PostSpecification {

    public static Specification<Post> search(PostSearchRequest request) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getTitle() != null && !request.getTitle().isEmpty()) {
                predicates.add(builder.like(root.get("title"), "%" + request.getTitle() + "%"));
            }

            if (request.getNickname() != null && !request.getNickname().isEmpty()) {
                Join<Object, Object> memberJoin = root.join("member", JoinType.INNER);
                predicates.add(builder.like(memberJoin.get("nickname"), "%" + request.getNickname() + "%"));
            }

            if (request.getCountry() != null && !request.getCountry().isEmpty()) {
                predicates.add(builder.equal(root.get("country"), request.getCountry()));
            }

            if (request.getPostId() != null) {
                predicates.add(builder.equal(root.get("postId"), request.getPostId()));
            }

            if (request.getStartDate() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("startDate"), request.getStartDate()));
            }

            if (request.getEndDate() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("endDate"), request.getEndDate()));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
