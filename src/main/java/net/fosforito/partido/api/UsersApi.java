package net.fosforito.partido.api;

import net.fosforito.partido.model.user.CurrentUserContext;
import net.fosforito.partido.model.user.User;
import net.fosforito.partido.model.user.UserDTO;
import net.fosforito.partido.model.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

@RestController
public class UsersApi {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CurrentUserContext currentUserContext;

  @Inject
  public UsersApi(UserRepository userRepository,
                  PasswordEncoder passwordEncoder,
                  CurrentUserContext currentUserContext) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.currentUserContext = currentUserContext;
  }

  @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON)
  @PreAuthorize("hasRole('ADMIN')")
  public Page<User> getUsers(Pageable pageable) {
    return userRepository.findAll(pageable);
  }

  @GetMapping(value = "/currentuser", produces = MediaType.APPLICATION_JSON)
  public User getCurrentUser() {
    return currentUserContext.getCurrentUser();
  }

  @PostMapping(value = {"/users"}, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
  public User createUser(@RequestBody UserDTO userDTO) {
    User user = new User();
    user.setUsername(userDTO.getUsername());
    user.setEmail(userDTO.getEmail());
    user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
    user.setActive(true); //TODO: verify Email
    return userRepository.save(user);
  }

  @DeleteMapping(value = "/users/{userId}")
  @PreAuthorize("@securityService.userIsSameUser(principal, #userId) OR hasRole('ADMIN')")
  public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
    //TODO: what will be deleted? Or just anonymize data?
    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isPresent()) {
      userOptional.map(user -> {
        userRepository.delete(user);
        return ResponseEntity.ok().build();
      });
    }
    return ResponseEntity.notFound().build();
  }

  /**
   * ADMIN only
   *
   * To see user details from a group member, the users shipped
   * with the getGroup-Request should be used.
   *
   * To see own user's details, the /currentuser endpoint should be used
   *
   * @param userId ID of the user to be fetched
   * @return User object or null if not existing
   */
  @GetMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON)
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<User> getUser(@PathVariable Long userId) {
    Optional<User> userOptional = userRepository.findById(userId);
    return userOptional.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
  }

  @PutMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
  @PreAuthorize("@securityService.userIsSameUser(principal, #userId)")
  public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
    Optional<User> userOptional = userRepository.findById(userId);
    if (passwordEncoder.matches(userDTO.getPassword(), userOptional.get().getPassword())) {
      return new ResponseEntity<>(userOptional.map(user -> {
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        if (userDTO.getNewPassword() != null && userDTO.getNewPassword().length() > 6) {
          user.setPassword(passwordEncoder.encode(userDTO.getNewPassword()));
        }
        return userRepository.save(user);
      }).get(), HttpStatus.OK);
    }
    return new ResponseEntity<>(userOptional.get(), HttpStatus.NOT_MODIFIED);
  }
}
