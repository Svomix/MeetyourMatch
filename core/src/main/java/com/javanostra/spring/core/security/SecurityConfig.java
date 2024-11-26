package com.javanostra.spring.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javanostra.spring.core.dto.ExceptionDTO;
import com.javanostra.spring.core.dto.ResponseDTO;
import com.javanostra.spring.core.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

    ObjectMapper mapper = new ObjectMapper();

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ContextRepository contextRepository, AuthMiddlewareFilter middlewareFilter) throws Exception {
        http
                .csrf((csrf) -> csrf.disable()) //TODO: add csrf
                .cors((cors) -> cors.disable()) //TODO: add cors
                .securityContext((context) -> context.securityContextRepository(contextRepository))
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(middlewareFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((requests) ->
                        requests
                                .requestMatchers("/api/account**").authenticated()
                                .anyRequest().permitAll()
                )
                .formLogin((form) -> form
                        .successHandler(
                                (request, response, authentication) -> {
                                    response.setStatus(HttpServletResponse.SC_OK);
                                    response.setContentType("application/json;charset=UTF-8");
                                    response.getWriter().write( mapper.writeValueAsString(new ResponseDTO(HttpStatus.OK, "logged in as "+authentication.getName())) );
                                }
                        )
                        .failureHandler(((request, response, exception) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write( mapper.writeValueAsString(new ExceptionDTO(exception.getMessage(), exception.getClass().getSimpleName(), HttpStatus.UNAUTHORIZED.value())) );
                        }))
                        .loginPage("/api/login")
                        .permitAll())
                .logout((logout) -> {
                    logout
                            .logoutUrl("/api/logout")
                            .permitAll()
                            .logoutSuccessHandler((request, response, authentication) -> {
                                response.setStatus(HttpServletResponse.SC_OK);
                                response.setContentType("application/json;charset=UTF-8");
                                response.getWriter().write( mapper.writeValueAsString(new ResponseDTO(HttpStatus.OK, "logged out")) );
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
