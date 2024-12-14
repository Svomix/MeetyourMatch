package com.javanostra.spring.core.controllers;

import com.javanostra.spring.core.entities.Tag;
import com.javanostra.spring.core.services.TagService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
@AllArgsConstructor
public class TagController {
    private final TagService tagService;
    
    @GetMapping
    public List<Tag> getAllTags() {
        return tagService.findAll();
    }

    @GetMapping("/{id}")
    public Tag getTag(@PathVariable long id) {
        return tagService.findById(id);
    }

    @PostMapping
    public void createTag(@RequestBody Tag tag) {
        tagService.save(tag);
    }

    @PutMapping
    public void updateTag(@RequestBody Tag tag) {
        tagService.save(tag);
    }

    @DeleteMapping("/{id}")
    public void deleteTag(@PathVariable long id) {
        tagService.deleteById(id);
    }
}
