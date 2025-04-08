package com.merveartut.task_manager.config;

import com.merveartut.task_manager.security.JwtAuthorizationFilter;
import com.merveartut.task_manager.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil);
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll() // Allow login without authentication
                        .requestMatchers("/api/users/v1/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/tasks/v1/project/{projectId}").hasAnyRole("PROJECT_MANAGER", "TEAM_LEADER", "TEAM_MEMBER")
                        .requestMatchers(HttpMethod.GET,"/api/tasks/v1").hasRole("PROJECT_MANAGER")
                        .requestMatchers(HttpMethod.GET,"/api/tasks/v1/task/{id}").hasAnyRole("PROJECT_MANAGER","TEAM_LEADER","TEAM_MEMBER")

                        .requestMatchers(HttpMethod.POST,"/api/tasks/**").hasRole("PROJECT_MANAGER")

                        .requestMatchers(HttpMethod.PUT,"/api/tasks/v1/set-assignee").hasRole("TEAM_LEADER")
                        .requestMatchers(HttpMethod.PUT, "/api/tasks/v1/set-state").hasAnyRole("TEAM_LEADER","TEAM_MEMBER")
                        .requestMatchers(HttpMethod.PUT, "/api/tasks/v1/set-priority").hasAnyRole("TEAM_LEADER","PROJECT_MANAGER")
                        .requestMatchers(HttpMethod.PUT,"/api/tasks/**").hasRole("PROJECT_MANAGER")

                        .requestMatchers("/api/projects/**").hasRole("PROJECT_MANAGER")


                        .requestMatchers(HttpMethod.POST,"/api/comments/v1/add-comment").permitAll()

                        .requestMatchers(HttpMethod.GET,"/api/comments/v1/{id}").hasRole("PROJECT_MANAGER")
                        .requestMatchers(HttpMethod.GET,"/api/comments/v1/task").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/comments/**").hasRole("PROJECT_MANAGER")
                        .requestMatchers("/api/attachments/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:5173")); // Allow frontend
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}