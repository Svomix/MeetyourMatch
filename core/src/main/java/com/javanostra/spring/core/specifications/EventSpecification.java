package com.javanostra.spring.core.specifications;

import com.javanostra.spring.core.entities.Event;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Objects;

@AllArgsConstructor
public class EventSpecification implements Specification<Event> {

    EventSearchCriteria searchCriteria;

    @Override
    public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if(Objects.nonNull(searchCriteria.getEventTitle())){
            String search = String.join("%", searchCriteria.getEventTitle().toLowerCase().split(" "));
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + search + "%");
        };
        return null;
    }
}
