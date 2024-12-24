package com.javanostra.spring.core.security;

import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UnconfirmedAndExpiredAccountFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;
    ContextRepository contextRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        SecurityContext context = contextRepository.loadContext(new HttpRequestResponseHolder(request, null)); //TODO: FIX

        if(Objects.nonNull(context)) {
            String username = ((User) context.getAuthentication().getPrincipal()).getUsername();

            User user = (User) userService.loadUserByUsername(username);
            if (!user.getIsEnabled() && user.getCreatedAt().before(Timestamp.from(Instant.now().minus(2, ChronoUnit.HOURS)))) {
                userService.delete(user);
                SecurityContextHolder.setContext(null);
                contextRepository.saveContext(new SecurityContextImpl(null), request, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}