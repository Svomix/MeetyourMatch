package com.javanostra.spring.core.specifications;

import com.javanostra.spring.core.entities.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;


@AllArgsConstructor
public class UserSpecification implements Specification<User> {

    UserSearchCriteria searchCriteria;

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if(Objects.nonNull(searchCriteria.getUsername())){
            return criteriaBuilder.like(root.get("username"), "%" + searchCriteria.getUsername() + "%");
        };
        return null;
    }
}
