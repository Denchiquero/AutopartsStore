package ru.mirea.autopartsstore.common.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.mirea.autopartsstore.auth.security.JwtAuthenticationFilter;
import ru.mirea.autopartsstore.common.exception.SecurityErrorWriter;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            SecurityErrorWriter securityErrorWriter
    ) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .cors(Customizer.withDefaults())

                .sessionManagement(session ->
                session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                )
        )

                .exceptionHandling(ex -> ex

                        .authenticationEntryPoint(
                                (request, response, authException) ->
                                        securityErrorWriter.write(
                                                response,
                                                HttpStatus.UNAUTHORIZED,
                                                "Authentication required",
                                                request.getRequestURI()
                                        )
                        )

                        .accessDeniedHandler(
                                (request, response, accessDeniedException) ->
                                        securityErrorWriter.write(
                                                response,
                                                HttpStatus.FORBIDDEN,
                                                "Access denied",
                                                request.getRequestURI()
                                        )
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/actuator/health"
                        )
                        .permitAll()

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        )
                        .permitAll()

                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login"
                        )
                        .permitAll()

                        .requestMatchers(
                                "/api/auth/me"
                        )
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/parts/**",
                                "/api/manufacturers/**",
                                "/api/categories/**",
                                "/api/vin/**",
                                "/api/vehicles/**"
                        )
                        .permitAll()

                        .requestMatchers(
                                "/api/inventory/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/parts/**",
                                "/api/manufacturers/**",
                                "/api/categories/**",
                                "/api/vehicles/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/parts/**",
                                "/api/manufacturers/**",
                                "/api/categories/**",
                                "/api/vehicles/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/parts/**",
                                "/api/manufacturers/**",
                                "/api/categories/**",
                                "/api/vehicles/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/orders/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                "/api/orders/**"
                        )
                        .authenticated()

                        .requestMatchers(
                                "/api/customers/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                "/api/profile/**"
                        )
                        .authenticated()

                        .anyRequest()
                        .permitAll()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:5173")
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}