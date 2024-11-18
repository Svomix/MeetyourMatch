package com.javanostra.spring.core.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.services.UserService;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;

@AllArgsConstructor
@Component
public class ContextRepository implements SecurityContextRepository {
    private static final String ACCESS_TOKEN = "accessToken";

    JwtService jwtService;
    UserService userService;

    private @Nullable String extractTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Objects.nonNull(header)) {
            return header.substring(7);
        }
        return null;
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        String token = extractTokenFromHeader(requestResponseHolder.getRequest());

        if (Objects.nonNull(token)) {
            try {
                String username = jwtService.extractToken(token);

                UserDetails u = userService.loadUserByUsername(username);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        u, //TODO: cache
                        null,
                        u.getAuthorities());

                return new SecurityContextImpl(authentication);
            } catch (JWTVerificationException e) {
                System.out.println(e);
            } catch (UsernameNotFoundException e) {
                System.out.println(e);
            }
        }

        return null;
    }

    private Cookie getCookie(String name, String value){
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        return cookie;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = context.getAuthentication();
        if (Objects.nonNull(auth) && auth.isAuthenticated()) {
            response.addCookie(getCookie(ACCESS_TOKEN, jwtService.generateToken((User) auth.getPrincipal())));
        } else {
            response.addCookie(getCookie(ACCESS_TOKEN, ""));
        }
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        return Objects.nonNull(token);
    }
}
