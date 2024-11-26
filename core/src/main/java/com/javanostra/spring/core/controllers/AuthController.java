package com.javanostra.spring.core.controllers;

import com.javanostra.spring.core.dto.NewUserDTO;
import com.javanostra.spring.core.dto.ResponseDTO;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.exceptions.BaseCoreException;
import com.javanostra.spring.core.exceptions.UserAlreadyExistsException;
import com.javanostra.spring.core.security.ContextRepository;
import com.javanostra.spring.core.security.SecurityConfig;
import com.javanostra.spring.core.services.AuthorizationService;
import com.javanostra.spring.core.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/register")
@AllArgsConstructor
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AuthorizationService authorizationService;
    private final ContextRepository contextRepository;

    @PostMapping
    public ResponseEntity<ResponseDTO> register(@Valid @ModelAttribute NewUserDTO newUser, HttpServletRequest request, HttpServletResponse response) throws BaseCoreException {
        if (userService.userExists(newUser.getUsername()))
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");

        if (userService.userExistsByEmail(newUser.getEmail()))
            throw new UserAlreadyExistsException("Пользователь с такой почтой уже существует");

        User newUserEnt = new User();

        newUserEnt.setUsername(newUser.getUsername());
        newUserEnt.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUserEnt.setEmail(newUser.getEmail());

        newUserEnt.setAuthorities(Set.of(authorizationService.getDefaultGroup()));

        userService.createUser(newUserEnt);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(newUserEnt, null, newUserEnt.getAuthorities());

        token.setDetails(new WebAuthenticationDetails(request));

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
        contextRepository.saveContext(context, request, response);

        return ResponseEntity.ok(new ResponseDTO(HttpStatus.OK.value(), "created account " + newUser.getUsername()));
    }
}