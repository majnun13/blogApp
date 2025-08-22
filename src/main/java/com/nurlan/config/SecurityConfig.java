package com.nurlan.config;

import com.nurlan.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;                     // ← NEW
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // --- Senin mevcut açık uçların ---
    public static final String REGISTER = "/api/auth/register";
    public static final String LOGIN = "/api/auth/login";
    public static final String REFRESH_TOKEN = "/api/auth/refresh-token";
    public static final String VERIFY_EMAIL = "/api/auth/verify-email";
    public static final String RESEND_VERIFICATION = "/api/auth/resend-verification";
    public static final String FORGOT_PASSWORD = "/api/auth/forgot-password";
    public static final String RESET_PASSWORD = "/api/auth/reset-password";
    public static final String RESET_PASSWORD_VALIDATE = "/api/auth/reset-password/validate";

    // Swagger whitelist
    private static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**"              // (bazı setup'larda gerekli)
    };

    @Autowired private AuthenticationProvider authenticationProvider;
    @Autowired private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired private AuthenticationEntryPoint authEntyPoint; // senin alan adıyla bıraktım

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/posts/*/tags").authenticated()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(
                                REGISTER, LOGIN, REFRESH_TOKEN,
                                VERIFY_EMAIL, RESEND_VERIFICATION,
                                FORGOT_PASSWORD, RESET_PASSWORD,
                                RESET_PASSWORD_VALIDATE
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/mine").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/posts/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**").authenticated()

                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authEntyPoint))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173")); // FE origin
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("*")); // GET,POST,PUT,DELETE,OPTIONS...
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
