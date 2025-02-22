package com.sparta.deliveryapp.config;

import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class JpaAuditorConfig {

  @Bean
  public AuditorAware<String> auditorAware() {
    return new AuditorAwareImpl();
  }

  @Slf4j
  public static class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (authentication == null || !authentication.isAuthenticated()) {
        return Optional.empty();
      }

      Object principal = authentication.getPrincipal();

      if (!(principal instanceof UserDetailsImpl user)) {
        log.warn("Principal Type Casting Error {}", principal);
        return Optional.empty();
      }

      return Optional.ofNullable(user.getEmail());
    }
  }
}
