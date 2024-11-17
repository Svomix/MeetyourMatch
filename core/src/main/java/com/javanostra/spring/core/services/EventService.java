package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.AttributeDAO;
import com.javanostra.spring.core.dao.EventAttributeValueDAO;
import com.javanostra.spring.core.dao.EventDAO;
import com.javanostra.spring.core.entities.Attribute;
import com.javanostra.spring.core.entities.Event;
import com.javanostra.spring.core.entities.EventAttributeValue;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventService {
    private final EventDAO eventDAO;
    private final EventAttributeValueDAO eventAttributeValueDAO;
    private final AttributeDAO attributeDAO;

    public Page<Event> findAllEvents(Pageable pageable) {
        return eventDAO.findAll(pageable);
    }

    public Event findEventById(Long eventId) {
        return eventDAO.findById(eventId).orElseThrow(NoSuchElementException::new);
    }

    @Transactional
    public void saveEvent(Event event) {
        eventDAO.save(event);
    }

    @Transactional
    public void updateEvent(Event event) {
        eventDAO.save(event);
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        eventDAO.deleteById(eventId);
    }

    public Page<Attribute> findEventAttributesByEventId(Long eventId, Pageable pageable) {
        List<Attribute> attributeList = eventAttributeValueDAO
                .findByEvent(eventDAO.findById(eventId).orElseThrow(NoSuchElementException::new))
                .stream()
                .map(EventAttributeValue::getAttribute)
                .toList();

        final int start = (int) pageable.getOffset();
        final int end = Math.min((int) pageable.getOffset() + pageable.getPageSize(), attributeList.size());
        return new PageImpl<>(attributeList.subList(start, end), pageable, attributeList.size());
    }

    @Transactional
    public void deleteEventAttributeByAttrId(Long eventId, Long attrId) {
        eventAttributeValueDAO.deleteByEventAndAttributeId(eventDAO.findById(eventId).orElseThrow(NoSuchElementException::new), attrId);
    }

    @Transactional
    public void createEventAttributeValue(Long eventId, Long attrId, String value) {
        EventAttributeValue eventAttributeValue = new EventAttributeValue();
        eventAttributeValue.setEvent(eventDAO.findById(eventId).orElseThrow(NoSuchElementException::new));
        eventAttributeValue.setAttribute(attributeDAO.findAttributeById(attrId));
        eventAttributeValue.setValue(value);
        eventAttributeValueDAO.save(eventAttributeValue);
    }
}
