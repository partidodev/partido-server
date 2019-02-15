package net.fosforito.partido.model.user;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class CurrentUserContext {

  private final UserRepository userRepository;

  @Inject
  public CurrentUserContext(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUserName = authentication.getName();
      return userRepository.findByEmail(currentUserName);
    }
    return null;
  }
}
