package com.javanostra.spring.core.controllers;

import com.javanostra.spring.core.entities.Attribute;
import com.javanostra.spring.core.services.AttributeService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/attributes")
@AllArgsConstructor
public class AttributeController {

    private final AttributeService attributeService;

    @GetMapping
    public Page<Attribute> findAllAttributes(
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @RequestParam(value = "limit", defaultValue = "5") Integer limit
    ) {
        return attributeService.findAllAttributes(PageRequest.of(offset, limit));
    }

    @GetMapping("/{attr_id}")
    public Attribute findAttributeById(@PathVariable("attr_id") Long attrId) {
        return attributeService.findAttributeById(attrId);
    }

    @PostMapping
    public void saveAttribute(@RequestBody Attribute attribute) {
        attributeService.saveAttribute(attribute);
    }

    @PutMapping
    public void updateAttribute(@RequestBody Attribute attribute) {
        attributeService.updateAttribute(attribute);
    }

    @DeleteMapping
    public void deleteAttributeById(@RequestParam Long id) {
        attributeService.deleteAttributeById(id);
    }
}
