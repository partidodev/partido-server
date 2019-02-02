package net.fosforito.partido.api;

import net.fosforito.partido.model.user.User;
import net.fosforito.partido.model.user.UserDTO;
import net.fosforito.partido.model.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

@RestController
public class UsersApi {

  @Inject
  private UserRepository userRepository;

  @Inject
  private PasswordEncoder passwordEncoder;

  @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON)
  public Page<User> usersGet(Pageable pageable) {
    return userRepository.findAll(pageable);
  }

  @PostMapping(value = {"/users", "/registration"}, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
  public User usersPost(@RequestBody User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  @DeleteMapping(value = "/users/{userId}")
  public ResponseEntity<?> usersUserIdDelete(@PathVariable Long userId) throws Exception {
    return userRepository.findById(userId)
        .map(user -> {
          userRepository.delete(user);
          return ResponseEntity.ok().build();
        }).orElseThrow(() -> new Exception("User not found with id " + userId));
  }

  @GetMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON)
  public User usersUserIdGet(@PathVariable Long userId) {
    return userRepository.findById(userId).get();
  }

  @PutMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
  public User usersUserIdPut(@PathVariable Long userId, @RequestBody UserDTO userDTO) throws Exception {
    return userRepository.findById(userId)
        .map(user -> {
          user.setFirstNames(userDTO.getFirstNames());
          user.setLastNames(userDTO.getLastNames());
          user.setEmail(userDTO.getEmail());
          user.setPassword(userDTO.getPassword());
          user.setBirthday(userDTO.getBirthday());
          return userRepository.save(user);
        }).orElseThrow(() -> new Exception("User not found with id " + userId));
  }
}
