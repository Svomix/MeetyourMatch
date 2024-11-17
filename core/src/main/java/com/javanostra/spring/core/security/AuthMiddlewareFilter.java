package com.javanostra.spring.core.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@AllArgsConstructor
@Service
public class AuthMiddlewareFilter extends OncePerRequestFilter {
    ContextRepository contextRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws AccessDeniedException, ServletException, IOException {
        SecurityContext context = contextRepository.loadContext(new HttpRequestResponseHolder(request, null));

        if(Objects.isNull(context)){
            throw new org.springframework.security.access.AccessDeniedException("invalid token");
        }

        SecurityContextHolder.setContext(context);
        filterChain.doFilter(request, response);
    }
}
