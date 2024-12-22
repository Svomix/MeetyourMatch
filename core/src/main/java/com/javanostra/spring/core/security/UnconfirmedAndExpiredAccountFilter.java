package com.javanostra.spring.core.security;

import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Component
@AllArgsConstructor
public class UnconfirmedAndExpiredAccountFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<Cookie> cookieToken = getAccessTokenCookie(request);
        if (cookieToken.isPresent()) {
            Cookie cookie = cookieToken.get();
            String username = jwtService.extractToken(cookie.getValue());

            User user = (User) userService.loadUserByUsername(username);
            if (!user.getIsEnabled() && user.getCreatedAt().isBefore(LocalDateTime.now().minusHours(2))) {
                userService.delete(user);
                deleteAccessTokenCookie(cookie, response);
            }
        }
        filterChain.doFilter(request, response);
    }

    private Optional<Cookie> getAccessTokenCookie(HttpServletRequest request) {
        Optional<Cookie[]> optionalCookies = Optional.ofNullable(request.getCookies());

        if (optionalCookies.isPresent()) {
            Cookie[] cookies = optionalCookies.get();
            return Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("accessToken"))
                    .findFirst();
        }
        else {
            return Optional.empty();
        }
    }

    private void deleteAccessTokenCookie(Cookie cookie, HttpServletResponse response) {
        cookie.setValue(null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}