package com.javanostra.spring.core.specifications;

import com.javanostra.spring.core.entities.Tag;
import com.javanostra.spring.core.exceptions.NoSuchTagException;
import com.javanostra.spring.core.services.TagService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class EventSearchCriteria {
    public static EventSearchCriteria fromString(String query, TagService tagService) {
        if(Objects.isNull(query)) return new EventSearchCriteria();
        String[] split = query.split("\\s");
        String newQuery = "";
        List<Tag> tags = new ArrayList<>();
        for (String s : split) {
            if(s.startsWith("#")){
                try {
                    Tag tag = tagService.findByNameIgnoreCase(s.substring(1));
                    tags.add(tag);
                }catch (NoSuchTagException e){

                }
            }else{
                newQuery += s + " ";
            }
        }
        return new EventSearchCriteria(newQuery, tags);
    }
    private String eventTitle;
    private List<Tag> eventTags;
}
