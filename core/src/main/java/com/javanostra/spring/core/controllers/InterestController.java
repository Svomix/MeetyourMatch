package com.javanostra.spring.core.controllers;

import com.javanostra.spring.core.entities.UserInterest;
import com.javanostra.spring.core.services.InterestService;
import com.javanostra.spring.core.services.UserService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
public class InterestController {
    @NonNull
    private final InterestService interestService;
    @NonNull
    private final UserService userService;

    @GetMapping
    public Set<UserInterest> getAllInterests() {
        return interestService.getAllUserInterests();
    }
}
