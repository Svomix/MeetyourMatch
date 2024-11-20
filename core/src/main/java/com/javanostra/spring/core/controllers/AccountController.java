package com.javanostra.spring.core.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javanostra.spring.core.dto.ResponseDTO;
import com.javanostra.spring.core.dto.UserProfileDTO;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.services.CityService;
import com.javanostra.spring.core.services.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.Objects;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    @NonNull
    private final UserService userService;
    @NonNull
    private final CityService cityService;
    ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/getInfo")
    public ResponseEntity<UserProfileDTO> getAccountInfo() {
        User currentUser = userService.getCurrentUser();

        if(Objects.nonNull(currentUser))
            return ResponseEntity.ok(mapper.convertValue(currentUser, UserProfileDTO.class));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/setCity")
    public ResponseEntity<ResponseDTO> setCity(@RequestParam("city") Long city_id){
        try {
            User currentUser = userService.getCurrentUser();

            if (Objects.nonNull(currentUser)) {
                currentUser.setCity(cityService.findCityById(city_id));
                userService.updateUser(currentUser);
                return ResponseEntity.ok(new ResponseDTO(HttpStatus.OK, "city set"));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }catch (NoSuchElementException exception){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(HttpStatus.BAD_REQUEST, "no such city exists"));
        }
    }
}
