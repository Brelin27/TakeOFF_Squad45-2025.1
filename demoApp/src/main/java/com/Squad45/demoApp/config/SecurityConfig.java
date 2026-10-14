package com.Squad45.demoApp.config;

import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.Squad45.demoApp.service.JwtTokenService;
import com.Squad45.demoApp.service.UsuarioService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenService jwtTokenService;
    private final UsuarioService usuarioService;

    public SecurityConfig(JwtTokenService jwtTokenService, @Lazy UsuarioService usuarioService) {
        this.jwtTokenService = jwtTokenService;
        this.usuarioService = usuarioService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(org.springframework.security.config.Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers("/registro/proprietario").permitAll()
                .requestMatchers("/registro/administrador").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/admin/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/propriedades").hasAnyRole("ADMINISTRADOR", "PROPRIETARIO")
                .anyRequest().authenticated())
            .addFilterBefore(new JwtAuthorizationFilter(jwtTokenService, usuarioService),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
        @Value("${FRONTEND_URL:http://localhost:5500}") String frontendUrl) {

    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(Arrays.asList(
        frontendUrl,
        "http://localhost:5500",
        "http://127.0.0.1:5500",
        "http://localhost:3000"
    ));

    configuration.setAllowedMethods(Arrays.asList(
        "GET", "POST", "PUT", "DELETE", "OPTIONS"
    ));

    configuration.setAllowedHeaders(Arrays.asList(
        "Authorization", "Content-Type", "X-Requested-With"
    ));

    configuration.setAllowCredentials(false);

    UrlBasedCorsConfigurationSource source =
        new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", configuration);
    return source;
}

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
