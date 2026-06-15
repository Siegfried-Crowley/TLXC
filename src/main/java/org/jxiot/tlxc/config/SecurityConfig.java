package org.jxiot.tlxc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthFilter) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // Public auth endpoints
                .antMatchers("/api/auth/**").permitAll()
                // Public API endpoints (no authentication required)
                .antMatchers("/api/problems/**").permitAll()
                .antMatchers("/api/contests/**").permitAll()
                .antMatchers("/api/rankings/**").permitAll()
                // Static resources & JSP pages
                .antMatchers("/css/**", "/js/**", "/static/**", "/images/**").permitAll()
                .antMatchers("/", "/*.jsp", "/WEB-INF/views/**").permitAll()
                // All other API endpoints require authentication
                .antMatchers("/api/**").authenticated()
                // Everything else is public
                .anyRequest().permitAll()
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
