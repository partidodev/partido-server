package net.fosforito.partido.api;

import net.fosforito.partido.mail.EmailService;
import net.fosforito.partido.model.bill.BillRepository;
import net.fosforito.partido.model.group.GroupRepository;
import net.fosforito.partido.model.user.*;
import net.fosforito.partido.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

@RestController
public class UsersApi {

  public static final String PARTIDO_API_BASE = "https://partido.rocks/api/";
  public static final String USERS_API_PATH = "users/";
  public static final String VERIFY_PATH = "/verify/";

  private final UserService userService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CurrentUserContext currentUserContext;
  private final EmailService emailService;
  private final GroupsApi groupsApi;
  private final GroupRepository groupRepository;
  private final BillRepository billRepository;

  @Inject
  public UsersApi(UserService userService,
                  UserRepository userRepository,
                  PasswordEncoder passwordEncoder,
                  CurrentUserContext currentUserContext,
                  EmailService emailService,
                  GroupsApi groupsApi,
                  GroupRepository groupRepository,
                  BillRepository billRepository) {
    this.userService = userService;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.currentUserContext = currentUserContext;
    this.emailService = emailService;
    this.groupsApi = groupsApi;
    this.groupRepository = groupRepository;
    this.billRepository = billRepository;
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
    Pair<String, User> validationOfCreatedUser = userService.createUser(userDTO);
    if (validationOfCreatedUser.getFirst() == null) {
      return new ResponseEntity<>(validationOfCreatedUser.getSecond(), HttpStatus.OK);
    } else {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
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
    Pair<String, User> validateDeletedUser = userService.deleteUser(userId);
    if (validateDeletedUser.getFirst() != null) {
      String errorMessage = validateDeletedUser.getFirst();
      if (errorMessage.equals("NoSuchUserExists")) {
        return ResponseEntity.notFound().build();
      } else if (errorMessage.equals("UserHasDebt")) {
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
      }
    }

    return ResponseEntity.ok().build();
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
    Pair<String, User> validateUpdatedUser = userService.updateUser(userId, userDTO);
    if (validateUpdatedUser.getFirst() != null) {
      String validationMessage = validateUpdatedUser.getFirst();

      if (validationMessage.equals("UpdatedEmail")) {
        return new ResponseEntity<>(validateUpdatedUser.getSecond(), HttpStatus.PRECONDITION_FAILED);
      }

      if (validationMessage.equals("NothingUpdated")) {
        return new ResponseEntity<>(validateUpdatedUser.getSecond(), HttpStatus.NOT_MODIFIED);
      }

      if (validationMessage.equals("NothingUpdated")) {
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
      }
    }

    return new ResponseEntity<>(validateUpdatedUser.getSecond(), HttpStatus.OK);
  }
}
