package ru.gb.timesheet.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

  @Bean
  GrantedAuthorityDefaults grantedAuthorityDefaults() {
    return new GrantedAuthorityDefaults("");
  }

  @Bean
  SecurityFilterChain noSecurity(HttpSecurity http) throws Exception {
    return http
      .csrf(AbstractHttpConfigurer::disable)
      .authorizeHttpRequests(it -> it.anyRequest().permitAll())
      .build();
  }

  //  @Bean
//  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//    return http
//      .authorizeHttpRequests(requests -> requests
////          .requestMatchers("/projects/**").hasAuthority(Role.ADMIN.getName())
////        .requestMatchers("/projects/**").hasRole("admin") // MY_ROLE_PREFIX_admin
//          .requestMatchers("/timesheets/**").hasAnyAuthority(Role.USER.getName())
//          .anyRequest().authenticated()
//      )
//      .formLogin(Customizer.withDefaults())
//      .build();
//  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
