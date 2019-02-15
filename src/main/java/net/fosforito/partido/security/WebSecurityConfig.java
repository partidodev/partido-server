package net.fosforito.partido.security;

import net.fosforito.partido.model.user.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.inject.Inject;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final UserDetailsServiceImpl userDetailsService;
  private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
  private final MySavedRequestAwareAuthenticationSuccessHandler successHandler;

  private SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();

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
    http.csrf().disable()
        .exceptionHandling()
        .authenticationEntryPoint(restAuthenticationEntryPoint)
        .and()
        .authorizeRequests()
        .antMatchers("/users").permitAll()
        .anyRequest().authenticated()
        .and()
        .formLogin()
        .successHandler(successHandler)
        .failureHandler(failureHandler)
        .and()
        .logout();
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
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
