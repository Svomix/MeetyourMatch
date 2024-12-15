package com.javanostra.spring.core.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.javanostra.spring.core.dto.ResponseDTO;
import com.javanostra.spring.core.dto.FullUserProfileDTO;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.entities.UserInterest;
import com.javanostra.spring.core.exceptions.BaseCoreException;
import com.javanostra.spring.core.exceptions.UserAlreadyExistsException;
import com.javanostra.spring.core.security.ContextRepository;
import com.javanostra.spring.core.services.AuthenticationService;
import com.javanostra.spring.core.services.CityService;
import com.javanostra.spring.core.services.InterestService;
import com.javanostra.spring.core.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    @NonNull
    private final UserService userService;
    @NonNull
    private final CityService cityService;
    @NonNull
    private final ContextRepository contextRepository;
    @NonNull
    private final PasswordEncoder passwordEncoder;
    @NonNull
    private final InterestService interestService;
    @NonNull
    private final AuthenticationService authenticationService;

    ObjectMapper mapper = new ObjectMapper();
    {
        mapper.registerModule(new Hibernate6Module()); //TODO: move to a bean / class
    }

    @GetMapping("/getInfo")
    public ResponseEntity<FullUserProfileDTO> getAccountInfo() {
        User currentUser = userService.getCurrentUser();

        if(Objects.nonNull(currentUser))
            return ResponseEntity.ok(mapper.convertValue(currentUser, FullUserProfileDTO.class));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("/interests")
    public ResponseEntity<Set<UserInterest>> getMyInterests() {
        User currentUser = userService.getCurrentUser();

        if(Objects.nonNull(currentUser))
            return ResponseEntity.ok(interestService.getUserInterests(currentUser));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/interests")
    public ResponseEntity<String> addInterest(@RequestParam("id") Integer id) throws BaseCoreException {
        User currentUser = userService.getCurrentUser();

        if(Objects.nonNull(currentUser)) {
            UserInterest interest = interestService.getUserInterestById(id);
            interestService.addUserInterest(currentUser, interest);
            return ResponseEntity.ok("success");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @DeleteMapping("/interests")
    public ResponseEntity<String> removeInterest(@RequestParam("id") Integer id) throws BaseCoreException {
        User currentUser = userService.getCurrentUser();

        if(Objects.nonNull(currentUser)) {
            UserInterest interest = interestService.getUserInterestById(id);
            interestService.removeUserInterest(currentUser, interest);
            return ResponseEntity.ok("success");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/setCity")
    public ResponseEntity<ResponseDTO> setCity(@RequestParam("city") Long city_id){
        try {
            User currentUser = userService.getCurrentUser();

            if (Objects.nonNull(currentUser)) {
                currentUser.setCity(cityService.findCityById(city_id));
                userService.updateUser(currentUser);
                return ResponseEntity.ok(new ResponseDTO(HttpStatus.OK.value(), "Город обновлён!"));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO(HttpStatus.UNAUTHORIZED.value(), "Нет авторизации!"));
        }catch (NoSuchElementException exception){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(HttpStatus.BAD_REQUEST.value(), "Такого города не существует!"));
        }
    }

    @PostMapping("/setEmail")
    public ResponseDTO setEmail(@NonNull @RequestParam("email") String email, HttpServletRequest request, HttpServletResponse response) throws BaseCoreException {
        if(userService.userExistsByEmail(email))
            throw new UserAlreadyExistsException("Пользователь с такой почтой уже существует");

        User user = userService.getCurrentUser();

        user.setEmail(email);
        userService.updateUser(user);

        authenticationService.UpdateToken(user, request, response);

        return new ResponseDTO(HttpStatus.OK.value(), "changed username successfully");
    }

    @PostMapping("/setImage")
    public ResponseEntity<ResponseDTO> setImage(@RequestParam("avatar_path") String avatar_path){
        try {
            User currentUser = userService.getCurrentUser();

            if (Objects.nonNull(currentUser)) {
                currentUser.setAvatarPath(avatar_path);
                userService.updateUser(currentUser);
                return ResponseEntity.ok(new ResponseDTO(HttpStatus.OK.value(), "Image set"));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (NoSuchElementException exception){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(HttpStatus.BAD_REQUEST.value(), "no image"));
        }
    }

    @PostMapping("/setName")
    public ResponseDTO setUsername(@NonNull @RequestParam("username") String username, HttpServletRequest request, HttpServletResponse response) throws BaseCoreException {
        if(userService.userExists(username))
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");

        User user = userService.getCurrentUser();

        user.setUsername(username);
        userService.updateUser(user);

        authenticationService.UpdateToken(user, request, response);

        return new ResponseDTO(HttpStatus.OK.value(), "changed username successfully");
    }

    @PostMapping("/setPassword")
    public ResponseDTO setPassword(@NonNull @RequestParam("password") String password, HttpServletRequest request, HttpServletResponse response){
        User user = userService.getCurrentUser();

        authenticationService.ChangePassword(user, passwordEncoder.encode(password));

        authenticationService.UpdateToken(user, request, response);

        return new ResponseDTO(HttpStatus.OK.value(), "changed password successfully");
    }
}
