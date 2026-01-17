package com.dulcefina.config;

import com.dulcefina.security.JwtAuthenticationFilter;
import com.dulcefina.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

          private final UserDetailsServiceImpl userDetailsService;

          public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
                      this.userDetailsService = userDetailsService;
          }

          @Bean
          public PasswordEncoder passwordEncoder() {
              return new BCryptPasswordEncoder();
          }

          @Bean
          public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                    return config.getAuthenticationManager();
          }

          @Bean
          public JwtAuthenticationFilter jwtAuthenticationFilter() {
                    return new JwtAuthenticationFilter(userDetailsService);
          }

         @Bean
         public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
             http
                     .cors(cors -> cors.disable())
                     .csrf(csrf -> csrf.disable())
                     .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                     .authorizeHttpRequests(auth -> auth
                             .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                             .requestMatchers("/users/**").permitAll()
                             .requestMatchers("/products/**").permitAll()
                             .requestMatchers("/api/images/**").permitAll()
                             .requestMatchers("/configurator/**").permitAll()
                             .requestMatchers("/cart/**").permitAll()
                             .requestMatchers("/orders/**").permitAll()
                             .requestMatchers("/favorites/**").permitAll()
                             .requestMatchers("/dashboard/**").permitAll()
                             .requestMatchers("/suppliers/**").permitAll()
                             .requestMatchers("/notifications/**").permitAll()
                             .requestMatchers("/payments/**").permitAll()
                             .requestMatchers("/stripe/**").permitAll()
                             .requestMatchers("/admin/**").hasRole("ADMIN")
                             .anyRequest().authenticated()
                     )
                     .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

             return http.build();
         }

}