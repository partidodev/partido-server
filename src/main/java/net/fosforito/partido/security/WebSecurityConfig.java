package net.fosforito.partido.security;

import net.fosforito.partido.model.user.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Value("${partido.remember_me_token_generation_key}")
  private String rememberMeTokenGenerationKey;

  private final UserDetailsServiceImpl userDetailsService;
  private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
  private final MySavedRequestAwareAuthenticationSuccessHandler successHandler;

  @Inject
  public WebSecurityConfig(UserDetailsServiceImpl userDetailsService,
                           RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                           MySavedRequestAwareAuthenticationSuccessHandler successHandler) {
    this.userDetailsService = userDetailsService;
    this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
    this.successHandler = successHandler;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors() // by default uses a Bean by the name of corsConfigurationSource
        .and()
        .exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint)
        .and()
        .formLogin()
        .successHandler(successHandler)
        .failureHandler(customAuthenticationFailureHandler())
        .and()
        .logout() // default on /logout path
        .deleteCookies("JSESSIONID")
        .and()
        .csrf().disable()
        .authorizeRequests()
        .antMatchers("/users").permitAll()
        .antMatchers("/actuator/health").permitAll()
        .anyRequest().authenticated()
        .and()
        .rememberMe()
        .key(rememberMeTokenGenerationKey) // secret for token generation
        .tokenValiditySeconds(31536000); // 1 year
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring()
      .antMatchers("/users/*/verify/*")
      .antMatchers("/users/*/reset-password")
      .antMatchers("/users/*/reset-password/*");
  }

  @Bean
  public CorsFilter corsFilter() {
      final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
      config.setAllowCredentials(true);
      config.setAllowedOriginPatterns(List.of("*"));
      config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE"));
      source.registerCorsConfiguration("/**", config);
      return new CorsFilter(source);
  }

  @Inject
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
  }

  @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public AuthenticationFailureHandler customAuthenticationFailureHandler() {
    return new CustomAuthenticationFailureHandler();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
