package net.fosforito.partido.api;

import net.fosforito.partido.mail.EmailService;
import net.fosforito.partido.model.group.Group;
import net.fosforito.partido.model.report.Balance;
import net.fosforito.partido.model.report.Report;
import net.fosforito.partido.model.user.CurrentUserContext;
import net.fosforito.partido.model.user.User;
import net.fosforito.partido.model.user.UserDTO;
import net.fosforito.partido.model.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@RestController
public class UsersApi {

  public static final String PARTIDO_API_BASE = "https://partido.rocks/api/";
  public static final String USERS_API_PATH = "users/";
  public static final String VERIFY_PATH = "/verify/";

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CurrentUserContext currentUserContext;
  private final EmailService emailService;
  private final GroupsApi groupsApi;

  @Inject
  public UsersApi(UserRepository userRepository,
                  PasswordEncoder passwordEncoder,
                  CurrentUserContext currentUserContext,
                  EmailService emailService,
                  GroupsApi groupsApi) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.currentUserContext = currentUserContext;
    this.emailService = emailService;
    this.groupsApi = groupsApi;
  }

  /**
   * Get information "about me", the currently signed in user.
   * @return User object with profile details.
   */
  @GetMapping(value = "/currentuser", produces = MediaType.APPLICATION_JSON)
  public User getCurrentUser() {
    return currentUserContext.getCurrentUser();
  }

  /**
   * Open REST endpoint for all users (signed in or not).
   * Used to register new user accounts.
   * @param userDTO object with details of the user to be created
   * @return newly created user object
   */
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
    templateModel.put("verificationLink",
        PARTIDO_API_BASE + USERS_API_PATH + savedUser.getId() + VERIFY_PATH + emailVerificationCode);
    emailService.sendEmailVerificationMail(userDTO.getEmail(), templateModel);
    return new ResponseEntity<>(savedUser, HttpStatus.OK);
  }

  /**
   * Delete user if there are no groups with non
   * zero balances that must be settled up before.
   *
   * @param userId ID of user to delete
   * @return ResponseEntity with status code:
   *         - 200 if user was deleted
   *         - 404 if user was not found
   *         - 412 if there are groups with open balances
   */
  @DeleteMapping(value = "/users/{userId}")
  @PreAuthorize("@securityService.userIsSameUser(principal, #userId)")
  public ResponseEntity<?> deleteUser(@PathVariable Long userId) {

    List<Group> groups = groupsApi.getCurrentUsersGroups();

    for (Group group : groups) {
      Report report = groupsApi.getGroupReport(group.getId());
      for (Balance balance : report.getBalances()) {
        if (!(balance.getBalance().compareTo(BigDecimal.ZERO) == 0)) {
          return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }
      }
    }

    return userRepository.findById(userId)
        .map(user -> {
          userRepository.delete(user);
          return ResponseEntity.ok().build();
        }).orElse(
            ResponseEntity.notFound().build()
        );
  }

  /**
   * Update own user profile.
   * @param userId ID of the user to be updated
   * @param userDTO User object containing updated profile details
   * @return updated User object
   */
  @PutMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
  @PreAuthorize("@securityService.userIsSameUser(principal, #userId)")
  public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO) {

    Optional<User> userOptional = userRepository.findById(userId);
    boolean userEmailChanged = userOptional.isPresent() && !userOptional.get().getEmail().equals(userDTO.getEmail());

    // Check if user wants to change it's current email address and if the new one
    // is already registered. Return an error status code if it is.
    if (userEmailChanged && userRepository.findByEmail(userDTO.getEmail()) != null) {
      return new ResponseEntity<>(userOptional.get(), HttpStatus.PRECONDITION_FAILED);
    }

    // Allow account changes only when user enters his password correctly
    if (userOptional.isPresent() && passwordEncoder.matches(userDTO.getPassword(), userOptional.get().getPassword())) {
      // Resend a verification mail to user if the email address has been changed.
      String emailVerificationCode = UUID.randomUUID().toString();
      if (userEmailChanged) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("username", userDTO.getUsername());
        templateModel.put("verificationLink",
            PARTIDO_API_BASE + USERS_API_PATH + userOptional.get().getId() + VERIFY_PATH + emailVerificationCode);
        emailService.sendEmailVerificationMail(userDTO.getEmail(), templateModel);
      }
      return new ResponseEntity<>(userOptional.map(user -> {
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        // If the email address has been changed, change verification details in user's entity
        if (userEmailChanged) {
          user.setEmailVerificationCode(emailVerificationCode);
          user.setEmailVerified(false);
        }
        // If a new password has been provided and it is valid, save it
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
