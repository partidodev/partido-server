package net.fosforito.partido.model.user;

import net.fosforito.partido.model.role.Role;
import net.fosforito.partido.security.LoginAttemptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextListener;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
  public static final String TOO_MANY_FAILED_LOGIN_ATTEMPTS = "Too many failed login attempts. Blocked for 5 minutes.";
  public static final String ACCOUNT_NOT_VERIFIED = "This account has not been verified yet.";
  public static final String ACCOUNT_NOT_FOUND = "Login details invalid. Please try again.";

  private final UserRepository userRepository;
  private final LoginAttemptService loginAttemptService;
  private final HttpServletRequest request;

  @Inject
  public UserDetailsServiceImpl(
      UserRepository userRepository,
      LoginAttemptService loginAttemptService,
      HttpServletRequest request) {
    this.userRepository = userRepository;
    this.loginAttemptService = loginAttemptService;
    this.request = request;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) throws RuntimeException {

    String ip = getClientIP();
    if (loginAttemptService.isBlocked(ip)) {
      LOGGER.warn("{} has been blocked after entering 3 wrong passwords in 5 minutes", ip);
      throw new RuntimeException(TOO_MANY_FAILED_LOGIN_ATTEMPTS);
    }

    User user = userRepository.findByEmail(email);

    if(user == null) {
      LOGGER.warn("User {} does not exist (anymore), need new login data", email);
      throw new RuntimeException(ACCOUNT_NOT_FOUND);
    }

    if (!user.isEmailVerified()) {
      LOGGER.warn("Account {} has not been verified, login attempt cancelled", ip);
      throw new RuntimeException(ACCOUNT_NOT_VERIFIED);
    }

    Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

    if (user.getRoles() != null) {
      for (Role role : user.getRoles()) {
        grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
      }
    }

    return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), grantedAuthorities);
  }

  public String getClientIP() {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null){
      return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0];
  }

  @Bean
  public RequestContextListener requestContextListener(){
    return new RequestContextListener();
  }
}
