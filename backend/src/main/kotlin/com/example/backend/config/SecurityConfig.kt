package com.example.backend.config


import com.example.backend.services.CustomUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig(
  private val customUserDetailsService: CustomUserDetailsService
) {

  @Bean
  fun passwordEncoder(): PasswordEncoder {
    return BCryptPasswordEncoder()
  }

  @Bean
  fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager {
    return authConfig.authenticationManager
  }

  @Bean
  fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
    return http
      .cors { }
      .csrf { it.disable() }
      .authorizeHttpRequests {
        it.requestMatchers("/api/auth/**").permitAll()
          .requestMatchers("/api/**").permitAll() // Allow access to /api/tenants
          .anyRequest().authenticated()
      }
      .userDetailsService(customUserDetailsService)
      .build()
  }
}
