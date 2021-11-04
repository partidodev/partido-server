package net.fosforito.partido.security;

import net.fosforito.partido.model.user.UserDetailsServiceImpl;
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
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.inject.Inject;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

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
        .anyRequest().authenticated()
        .and()
        .rememberMe()
        .key("o#3vt9§q384tnzv%79384tz78t3*q7z983z&4894=)zvt783tt8&v") // secret for token generation
        .tokenValiditySeconds(31536000); // 1 year
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring()
      .antMatchers("/users/*/verify/*")
      .antMatchers("/users/*/reset-password");
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
    corsConfiguration.setAllowCredentials(true);
    corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfiguration);
    return source;
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
