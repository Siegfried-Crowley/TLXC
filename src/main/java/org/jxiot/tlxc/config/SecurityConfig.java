package org.jxiot.tlxc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Order(1)   // 提高优先级
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()   // 注意：如果 Spring Boot <2.7，使用 .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .anyRequest().permitAll()
                .and()
                .formLogin().disable()
                .httpBasic().disable();
        return http.build();
    }
}