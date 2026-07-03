package com.krishu.ecommerce.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

@Component
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean
    public BCryptPasswordEncoder getEncoding(){
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain securityFilter(HttpSecurity http){
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(request->request.requestMatchers("/auth/","/auth/signin","/auth/signup")
                .permitAll().anyRequest().authenticated());
        return http.build();
    }
}
