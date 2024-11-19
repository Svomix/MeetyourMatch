package com.javanostra.spring.core.controllers;

import com.javanostra.spring.core.dto.NewUserDTO;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.services.AuthorizationService;
import com.javanostra.spring.core.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/register")
@AllArgsConstructor
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AuthorizationService authorizationService;

    @PostMapping
    public ResponseEntity<String> register(@Valid @ModelAttribute NewUserDTO newUser) {
        if (!userService.userExists(newUser.getUsername())) {
            User newUserEnt = new User();

            newUserEnt.setUsername(newUser.getUsername());
            newUserEnt.setPassword(passwordEncoder.encode(newUser.getPassword()));
            newUserEnt.setEmail(newUser.getEmail());

            newUserEnt.setAuthorities(Set.of(authorizationService.getDefaultGroup()));

            System.out.println(newUserEnt);

            userService.createUser(newUserEnt);

            return ResponseEntity.ok("create user " + newUser.getUsername());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user " + newUser.getUsername() + " already exists");
        }
    }

}
