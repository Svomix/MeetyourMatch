package com.javanostra.spring.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.enums.Relation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelationUserDTO extends UserProfileDTO {
    private Relation myRelation;
    private Relation userRelation;

    public static RelationUserDTO createFromUserProfileDTO(UserProfileDTO user, Relation myRelation, Relation userRelation) {
        return RelationUserDTO
                .builder()
                .id(user.getId())
                .username(user.getUsername())
                .city(user.getCity())
                .gender(user.getGender())
                .description(user.getDescription())
                .avatarPath(user.getAvatarPath())
                .lastSeenAt(user.getLastSeenAt())
                .myRelation(myRelation)
                .userRelation(userRelation)
                .build();
    }
}
