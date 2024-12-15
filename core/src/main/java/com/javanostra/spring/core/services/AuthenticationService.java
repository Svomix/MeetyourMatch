package com.javanostra.spring.core.services;

import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.security.ContextRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {
    @NonNull
    private final UserService userService;
    @NonNull
    private final ContextRepository contextRepository;
    @NonNull
    private final PasswordEncoder passwordEncoder;

    public void UpdateToken(User user, HttpServletRequest request, HttpServletResponse response) {
        SecurityContext context = SecurityContextHolder.getContext();

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        token.setDetails(new WebAuthenticationDetails(request));

        context.setAuthentication(token);
        contextRepository.saveContext(context, request, response);
    }

    public void ChangePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updateUser(user);
    }



}
