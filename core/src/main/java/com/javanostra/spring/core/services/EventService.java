package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.EventDAO;
import com.javanostra.spring.core.entities.Event;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class EventService {
    private final EventDAO eventDAO;

    public List<Event> findAllEvents() {
        return eventDAO.findAll();
    }

    public Event findEventById(int id) {
        return eventDAO.findById(id).orElseThrow(NoSuchElementException::new);
    }

    public void saveEvent(Event event) {
        eventDAO.save(event);
    }

    public void updateEvent(Event event) {
        eventDAO.save(event);
    }

    public void deleteEvent(int id) {
        eventDAO.deleteById(id);
    }
}
