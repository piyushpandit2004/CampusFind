
package com.campusfind.campusfind.config;

import com.campusfind.campusfind.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();

                    // IntelliJ frontend ke liye CORS origins
                    config.setAllowedOrigins(List.of(
                            "http://localhost:63342",
                            "http://127.0.0.1:63342",
                            "http://localhost:5500",
                            "http://127.0.0.1:5500",
                            "http://localhost:5501",
                            "http://127.0.0.1:5501"
                    ));

                    config.setAllowedMethods(List.of(
                            "GET",
                            "POST",
                            "PUT",
                            "PATCH",
                            "DELETE",
                            "OPTIONS"
                    ));

                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(false);

                    return config;
                }))
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login"
                        ).permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                (request, response, authException) -> {
                                    System.out.println(
                                            "AUTHENTICATION FAILED: "
                                                    + request.getRequestURI()
                                    );

                                    System.out.println(
                                            "REASON: "
                                                    + authException.getMessage()
                                    );

                                    response.setStatus(
                                            HttpServletResponse.SC_UNAUTHORIZED
                                    );
                                    response.setContentType("application/json");

                                    response.getWriter().write(
                                            "{\"message\":\"Please provide a valid JWT token\"}"
                                    );
                                }
                        )
                        .accessDeniedHandler(
                                (request, response, accessDeniedException) -> {
                                    System.out.println(
                                            "ACCESS DENIED: "
                                                    + request.getRequestURI()
                                    );

                                    System.out.println(
                                            "REASON: "
                                                    + accessDeniedException.getMessage()
                                    );

                                    response.setStatus(
                                            HttpServletResponse.SC_FORBIDDEN
                                    );
                                    response.setContentType("application/json");

                                    response.getWriter().write(
                                            "{\"message\":\"Access denied: insufficient permissions\"}"
                                    );
                                }
                        )
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
