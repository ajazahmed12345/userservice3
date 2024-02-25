package com.ajaz.userservice3.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurity {

//    @Bean
//    public SecurityFilterChain filteringCriteria(HttpSecurity http) throws Exception{
////        http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/auth/logout").authenticated());
//        http.cors().disable();
//        http.csrf().disable();
//
//        http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll()).csrf(AbstractHttpConfigurer::disable);
//
//        return http.build();
//    }

    @Bean
    public BCryptPasswordEncoder createBCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }


}
