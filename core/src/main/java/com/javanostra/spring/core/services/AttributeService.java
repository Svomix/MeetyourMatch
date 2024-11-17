package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.AttributeDAO;
import com.javanostra.spring.core.entities.Attribute;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AttributeService {
    private final AttributeDAO attributeDAO;

    public Page<Attribute> findAllAttributes(Pageable pageable) {
        return attributeDAO.findAll(pageable);
    }

    public Attribute findAttributeById(Long attrId) {
        return attributeDAO.findAttributeById(attrId);
    }

    @Transactional
    public void saveAttribute(Attribute attribute) {
        attributeDAO.save(attribute);
    }

    @Transactional
    public void updateAttribute(Attribute attribute) {
        attributeDAO.save(attribute);
    }

    @Transactional
    public void deleteAttributeById(Long attrId) {
        attributeDAO.deleteAttributeById(attrId);
    }
}
