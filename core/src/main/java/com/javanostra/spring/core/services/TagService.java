package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.EventDAO;
import com.javanostra.spring.core.dao.TagDAO;
import com.javanostra.spring.core.entities.Tag;
import com.javanostra.spring.core.exceptions.NoSuchTagException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class TagService {
    private final TagDAO tagDAO;

    public Tag findById(long id) throws NoSuchTagException {
        return tagDAO.findById(id).orElseThrow(NoSuchTagException::new);
    }

    public Tag findByNameIgnoreCase(String name) throws NoSuchTagException {
        return tagDAO.findByNameIgnoreCase(name).orElseThrow(NoSuchTagException::new);
    }

    public List<Tag> findAll() {
        return tagDAO.findAll();
    }

    @Transactional
    public void save(Tag tag) {
        tagDAO.save(tag);
    }

    @Transactional
    public void update(Tag tag) {
        tagDAO.save(tag);
    }

    @Transactional
    public void deleteById(long id) {
        tagDAO.deleteById(id);
    }
}
