package com.sparta.deliveryapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }


  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http

        .csrf(csrf -> csrf.disable())
        // 요청에 대한 접근 권한을 설정합니다.
        .authorizeHttpRequests(authorize -> authorize
            // /auth/signIn 경로에 대한 접근을 허용합니다. 이 경로는 인증 없이 접근할 수 있습니다.
            .requestMatchers("/api/users/**").permitAll()
            // 그 외의 모든 요청은 인증이 필요합니다.
            .anyRequest().authenticated()
        );

    // 추후 session 없앤 후 jwt filter 추가
    // 설정된 보안 필터 체인을 반환합니다.
    return http.build();
  }


}
