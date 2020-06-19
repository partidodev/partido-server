package net.fosforito.partido.security;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

  private final LoginAttemptService loginAttemptService;

  public AuthenticationFailureListener(LoginAttemptService loginAttemptService) {
    this.loginAttemptService = loginAttemptService;
  }

  public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
    WebAuthenticationDetails auth = (WebAuthenticationDetails) e.getAuthentication().getDetails();
    loginAttemptService.loginFailed(auth.getRemoteAddress());
  }
}
