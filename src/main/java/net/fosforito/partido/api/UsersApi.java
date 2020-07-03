package net.fosforito.partido.api;

import net.fosforito.partido.mail.EmailService;
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
import java.util.*;

@RestController
public class UsersApi {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CurrentUserContext currentUserContext;
  private final EmailService emailService;

  @Inject
  public UsersApi(UserRepository userRepository,
                  PasswordEncoder passwordEncoder,
                  CurrentUserContext currentUserContext,
                  EmailService emailService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.currentUserContext = currentUserContext;
    this.emailService = emailService;
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
  public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO) {
    User user = new User();
    if (userRepository.findByEmail(userDTO.getEmail()) != null) {
      // Email already registered! Return error status
      // with empty user object because client handling is problematic else
      return new ResponseEntity<>(user, HttpStatus.PRECONDITION_FAILED);
    }
    String emailVerificationCode = UUID.randomUUID().toString();
    user.setUsername(userDTO.getUsername());
    user.setEmail(userDTO.getEmail());
    user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
    user.setRegistrationDate(new Date());
    user.setEmailVerified(false); //verify Email to activate account
    user.setEmailVerificationCode(emailVerificationCode);
    User savedUser = userRepository.save(user);
    Map<String, Object> templateModel = new HashMap<>();
    templateModel.put("username", userDTO.getUsername());
    templateModel.put("verificationLink", "https://partido.rocks/api/users/" + savedUser.getId() + "/verify/" + emailVerificationCode);
    emailService.sendEmailVerificationMail(userDTO.getEmail(), templateModel);
    return new ResponseEntity<>(savedUser, HttpStatus.OK);
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
   * <p>
   * To see user details from a group member, the users shipped
   * with the getGroup-Request should be used.
   * <p>
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
    // Check if user wants to change it's current email address. If yes, check if the new one
    // is already registered and return an error status code if it is.
    if (userOptional.isPresent()
        && !userOptional.get().getEmail().equals(userDTO.getEmail())) {
      if (userRepository.findByEmail(userDTO.getEmail()) != null) {
        return new ResponseEntity<>(userOptional.get(), HttpStatus.PRECONDITION_FAILED);
      }
    }
    // Allow email changes only when user enters his password correctly
    if (userOptional.isPresent()
        && passwordEncoder.matches(userDTO.getPassword(), userOptional.get().getPassword())) {
      return new ResponseEntity<>(userOptional.map(user -> {
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        if (userDTO.getNewPassword() != null && userDTO.getNewPassword().length() > 6) {
          user.setPassword(passwordEncoder.encode(userDTO.getNewPassword()));
        }
        return userRepository.save(user);
      }).get(), HttpStatus.OK);
    }
    // If nothing has changed, just return the current user if found
    return userOptional.map(user -> new ResponseEntity<>(user, HttpStatus.NOT_MODIFIED))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }
}
