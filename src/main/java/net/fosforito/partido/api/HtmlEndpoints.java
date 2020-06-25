package net.fosforito.partido.api;

import net.fosforito.partido.model.user.User;
import net.fosforito.partido.model.user.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class HtmlEndpoints {

  private final UserRepository userRepository;
  private final SpringTemplateEngine thymeleafTemplateEngine;

  @Inject
  public HtmlEndpoints(UserRepository userRepository, SpringTemplateEngine thymeleafTemplateEngine) {
    this.userRepository = userRepository;
    this.thymeleafTemplateEngine = thymeleafTemplateEngine;
  }

  /**
   * REST endpoint used to verify user email addresses.
   * User does not need to login to verify email.
   * @param userId ID of the user account to verify
   * @param verificationCode Verification code
   * @return HTML view with verification status
   */
  @ResponseBody
  @GetMapping(value = "/users/{userId}/verify/{verificationCode}", produces = MediaType.TEXT_HTML)
  public String verifyUserEmail(@PathVariable Long userId, @PathVariable String verificationCode) {
    Optional<User> userOptional = userRepository.findById(userId);
    Map<String, Object> templateModel = new HashMap<>();
    String message;
    if (userOptional.isPresent() && userOptional.get().getEmailVerificationCode().equals(verificationCode)) {
      userOptional.map(user -> {
        user.setEmailVerified(true);
        return userRepository.save(user);
      });
      message = "Your account has been verified successfully! Now you can start using the Partido App.";
      templateModel.put("username", userOptional.get().getUsername());
    } else {
      message = "Invalid verification details provided. Account cannot be verified.";
    }
    templateModel.put("message", message);
    Context thymeleafContext = new Context();
    thymeleafContext.setVariables(templateModel);
    return thymeleafTemplateEngine.process("email-verification-result.html", thymeleafContext);
  }
}
