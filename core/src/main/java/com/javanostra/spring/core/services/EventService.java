package com.javanostra.spring.core.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.javanostra.spring.core.dao.EventAttributeValueDAO;
import com.javanostra.spring.core.dao.EventCommentDAO;
import com.javanostra.spring.core.dao.EventDAO;
import com.javanostra.spring.core.dao.TagDAO;
import com.javanostra.spring.core.dto.CommentDTO;
import com.javanostra.spring.core.dto.EventDTO;
import com.javanostra.spring.core.dto.FullEventDTO;
import com.javanostra.spring.core.entities.*;
import com.javanostra.spring.core.exceptions.BaseCoreException;
import com.javanostra.spring.core.exceptions.NoSuchCommentException;
import com.javanostra.spring.core.exceptions.NoSuchEventException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.stereotype.Service;

import javax.xml.stream.events.Comment;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventService {
    private final EventDAO eventDAO;
    private final EventAttributeValueDAO eventAttributeValueDAO;
    private final EventCommentDAO eventCommentDAO;
    //private final AttributeDAO attributeDAO;
    private final TagDAO tagDAO;

    ObjectMapper mapper = new ObjectMapper();
    {
        mapper.registerModule(new Hibernate6Module()); //TODO: move to a bean / class
    }

    public List<Event> findAllEvents() {return eventDAO.findAll();}

    public Page<EventDTO> findAllEvents(Pageable pageable) {
        return eventDAO.findAll(pageable).map(a -> mapper.convertValue(a, EventDTO.class));
    }

    public Event findEventById(Long eventId) throws BaseCoreException {
        return eventDAO.findById(eventId).orElseThrow(NoSuchEventException::new);
    }

    public FullEventDTO findEventDtoById(Long eventId) throws BaseCoreException {
        Event event = findEventById(eventId);
        FullEventDTO result = mapper.convertValue(event, FullEventDTO.class);
        result.setComments(event.getComments().stream().map(a -> mapper.convertValue(a, CommentDTO.class)).toList());
        return result;
    }

    @Transactional
    public List<EventComment> getComments(Long eventId) throws BaseCoreException {
        Event event = eventDAO.findById(eventId).orElseThrow(NoSuchEventException::new);
        return event.getComments();
    }
    @Transactional
    public EventComment getComment(Integer commentId) throws BaseCoreException {
        return eventCommentDAO.findById(commentId).orElseThrow(NoSuchCommentException::new);
    }

    @Transactional
    public EventComment addComment(Long eventId, EventComment comment) throws BaseCoreException {
        Event event = eventDAO.findById(eventId).orElseThrow(NoSuchEventException::new);
        comment.setEvent(event);
        event.getComments().add(comment);
        eventDAO.save(event);
        return comment;
    }

    @Transactional
    public void removeComment(Long eventId, EventComment comment) throws BaseCoreException {
        Event event = eventDAO.findById(eventId).orElseThrow(NoSuchEventException::new);
        comment.setEvent(event);
        event.getComments().remove(comment);
        eventDAO.save(event);
        eventCommentDAO.delete(comment);
    }

//    public List<Tag> findEventTagsByEventId(Long userId) {
//        List<EventAttributeValue> EAVs = eventAttributeValueDAO.findByEventAndAttribute(eventDAO.findEventById(userId), attributeDAO.findAttributeById(1L));
//        List<Tag> tags = new ArrayList<>();
//        for (EventAttributeValue EAV : EAVs) {
//            tagDAO.findById(Long.parseLong(EAV.getValue())).ifPresent(tags::add);
//        }
//        return tags;
//    }

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

//    public Page<Attribute> findEventAttributesByEventId(Long eventId, Pageable pageable) {
//        List<Attribute> attributeList = eventAttributeValueDAO
//                .findByEvent(eventDAO.findById(eventId).orElseThrow(NoSuchElementException::new))
//                .stream()
//                .map(EventAttributeValue::getAttribute)
//                .toList();
//
//        final int start = (int) pageable.getOffset();
//        final int end = Math.min((int) pageable.getOffset() + pageable.getPageSize(), attributeList.size());
//        return new PageImpl<>(attributeList.subList(start, end), pageable, attributeList.size());
//    }
//
//    @Transactional
//    public void deleteEventAttributeByAttrId(Long eventId, Long attrId) {
//        eventAttributeValueDAO.deleteByEventAndAttributeId(eventDAO.findById(eventId).orElseThrow(NoSuchElementException::new), attrId);
//    }
//
//    @Transactional
//    public void createEventAttributeValue(Long eventId, Long attrId, String value) {
//        EventAttributeValue eventAttributeValue = new EventAttributeValue();
//        eventAttributeValue.setEvent(eventDAO.findById(eventId).orElseThrow(NoSuchElementException::new));
//        eventAttributeValue.setAttribute(attributeDAO.findAttributeById(attrId));
//        eventAttributeValue.setValue(value);
//        eventAttributeValueDAO.save(eventAttributeValue);
//    }
}
