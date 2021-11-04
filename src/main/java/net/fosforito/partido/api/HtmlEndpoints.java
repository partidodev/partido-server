package net.fosforito.partido.api;

import net.fosforito.partido.mail.EmailService;
import net.fosforito.partido.model.user.User;
import net.fosforito.partido.model.user.UserRepository;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
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
  private final EmailService emailService;
  private final PasswordEncoder passwordEncoder;

  @Inject
  public HtmlEndpoints(UserRepository userRepository,
      SpringTemplateEngine thymeleafTemplateEngine,
      EmailService emailService,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.thymeleafTemplateEngine = thymeleafTemplateEngine;
    this.emailService = emailService;
    this.passwordEncoder = passwordEncoder;
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
    String title;
    String message;
    if (userOptional.isPresent()
        && !userOptional.get().isEmailVerified()
        && userOptional.get().getEmailVerificationCode().equals(verificationCode)) {
      userOptional.map(user -> {
        user.setEmailVerified(true);
        return userRepository.save(user);
      });
      title = "Account verified";
      message = "Your account has been verified successfully! Now you can start using the Partido App.";
      templateModel.put("username", userOptional.get().getUsername());
    } else if (userOptional.isPresent()
        && userOptional.get().isEmailVerified()
        && userOptional.get().getEmailVerificationCode().equals(verificationCode)) {
      title = "Account already verified";
      message = "Your account has already been verified! You can start using the Partido App.";
      templateModel.put("username", userOptional.get().getUsername());
    } else {
      title = "Account verification failed";
      message = "Invalid verification details provided. Account cannot be verified. If you think, that's an error, please contact us.";
    }
    templateModel.put("title", title);
    templateModel.put("message", message);
    Context thymeleafContext = new Context();
    thymeleafContext.setVariables(templateModel);
    return thymeleafTemplateEngine.process("email-verification-result.html", thymeleafContext);
  }

  @ResponseBody
  @GetMapping(value = "/users/{userId}/reset-password/{resetPasswordCode}", produces = MediaType.TEXT_HTML)
  public String resetPassword(@PathVariable Long userId, @PathVariable String resetPasswordCode) {
    Optional<User> userOptional = userRepository.findById(userId);
    Map<String, Object> webTemplateModel = new HashMap<>();
    String title;
    String message;
    if (userOptional.isPresent()
        && userOptional.get().getResetPasswordCode() != null
        && userOptional.get().getResetPasswordCode().length() > 1
        && userOptional.get().getResetPasswordCode().equals(resetPasswordCode)) {
      
      String newPassword = RandomStringUtils.random(12, true, true);

      userOptional.map(user -> {
        user.setResetPasswordCode("");
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
      });

      Map<String, Object> mailTemplateModel = new HashMap<>();
      mailTemplateModel.put("username", userOptional.get().getUsername());
      mailTemplateModel.put("password", newPassword);
      emailService.sendNewPasswordMail(userOptional.get().getEmail(), mailTemplateModel);

      title = "Password reset";
      message = "Your password was reset successfully! A new password has been sent to you in a separate Email.";
      webTemplateModel.put("username", userOptional.get().getUsername());
    } else {
      title = "Password reset error";
      message = "Account does not exist or the password reset link is invalid. If you think, that's an error, please contact us.";
    }
    webTemplateModel.put("title", title);
    webTemplateModel.put("message", message);
    Context thymeleafContext = new Context();
    thymeleafContext.setVariables(webTemplateModel);
    return thymeleafTemplateEngine.process("reset-password-result.html", thymeleafContext);
  }
}
