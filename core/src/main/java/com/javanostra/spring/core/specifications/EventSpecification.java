package com.javanostra.spring.core.specifications;

import com.javanostra.spring.core.entities.Event;
import com.javanostra.spring.core.entities.Tag;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class EventSpecification implements Specification<Event> {

    EventSearchCriteria searchCriteria;

    @Override
    public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        ArrayList<Predicate> predicates = new ArrayList<>();
        if(Objects.nonNull(searchCriteria.getEventTitle())){
            String search = String.join("%", searchCriteria.getEventTitle().toLowerCase().split(" "));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + search + "%"));
        };
        if(Objects.nonNull(searchCriteria.getEventTags())) {
            for(Tag tag : searchCriteria.getEventTags()) {
                predicates.add(root.join("tags", JoinType.INNER).in(List.of(tag)));
            }
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
