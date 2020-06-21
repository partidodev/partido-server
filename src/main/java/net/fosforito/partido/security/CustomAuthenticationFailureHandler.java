package net.fosforito.partido.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static net.fosforito.partido.model.user.UserDetailsServiceImpl.ACCOUNT_NOT_VERIFIED;
import static net.fosforito.partido.model.user.UserDetailsServiceImpl.TOO_MANY_FAILED_LOGIN_ATTEMPTS;

public class CustomAuthenticationFailureHandler
    implements AuthenticationFailureHandler {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException exception)
      throws IOException {

    if (exception.getMessage().equals(TOO_MANY_FAILED_LOGIN_ATTEMPTS)) {
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    } else if (exception.getMessage().equals(ACCOUNT_NOT_VERIFIED)) {
      response.setStatus(HttpStatus.LOCKED.value());
    } else {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    Map<String, Object> data = new HashMap<>();
    data.put("message", exception.getMessage());

    response.getOutputStream().println(objectMapper.writeValueAsString(data));
  }
}
