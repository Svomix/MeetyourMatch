package com.javanostra.spring.core.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javanostra.spring.core.dao.UserGroupDAO;
import com.javanostra.spring.core.dto.NewUserDTO;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.services.GroupService;
import com.javanostra.spring.core.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/register")
@AllArgsConstructor
public class UserController {

    UserService userService;
    UserGroupDAO userGroupDAO;
    GroupService groupService;
    PasswordEncoder encoder;

    final ObjectMapper mapper;

    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody NewUserDTO newUser) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if(Objects.nonNull(auth) && auth.isAuthenticated()){
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("");
//        }

        User user =new User();
        user.setUsername(newUser.getUsername());
        user.setEmail(newUser.getEmail());
        user.setPassword(encoder.encode(newUser.getPassword()));

        user.setAuthorities(Set.of(groupService.getDefaultGroup()));

        userService.createUser(user);

        return ResponseEntity.ok("created");
    }
}
