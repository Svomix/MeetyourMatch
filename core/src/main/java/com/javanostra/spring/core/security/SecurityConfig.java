package com.javanostra.spring.core.security;

import com.javanostra.spring.core.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
//        System.out.println(encoder.encode("1257"));
        return encoder;
    }

    @Bean
    JwtService jwtService() {
        return new JwtService("secret");
    }

    @Autowired
    UserService userDetailsManager;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ContextRepository conextRepository, AuthMiddlewareFilter middlewareFilter) throws Exception {
        http
                .csrf((csrf) -> csrf.disable()) //TODO: add csrf
                .cors((cors) -> cors.disable()) //TODO: add cors
                .securityContext((context) -> context.securityContextRepository(conextRepository))
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(middlewareFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((requests) ->
                        requests
                                .requestMatchers("/api/v1/events**").permitAll()
                                .requestMatchers("/api/v1/**").authenticated()
                                .anyRequest().permitAll()
                )
                .formLogin((form) -> form
                        .successHandler(
                                (request, response, authentication) -> {
                                    response.setStatus(HttpServletResponse.SC_OK);
                                    response.getWriter().println("logged in as " + authentication.getName());
                                }
                        )
                        .failureHandler(((request, response, exception) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
                        }))
                        .loginPage("/api/login")
                        .permitAll())
                .logout((logout) -> {
                    logout
                            .logoutUrl("/api/logout")
                            .permitAll()
                            .logoutSuccessHandler((request, response, authentication) -> {
                                response.setStatus(HttpServletResponse.SC_OK);
                                response.getWriter().println("logged out");
                            });
                })
                .exceptionHandling((handling -> {
                    handling.authenticationEntryPoint(((request, response, authException) -> {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
                    }));
                }));

        return http.build();
    }
}
