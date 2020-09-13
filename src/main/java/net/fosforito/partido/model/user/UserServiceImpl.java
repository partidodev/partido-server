package net.fosforito.partido.model.user;

import net.fosforito.partido.api.GroupsApi;
import net.fosforito.partido.database.UTCDateService;
import net.fosforito.partido.mail.EmailService;
import net.fosforito.partido.model.bill.BillRepository;
import net.fosforito.partido.model.group.Group;
import net.fosforito.partido.model.group.GroupRepository;
import net.fosforito.partido.model.report.Balance;
import net.fosforito.partido.model.report.Report;
import net.fosforito.partido.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;

import static net.fosforito.partido.api.UsersApi.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserContext currentUserContext;
    private final EmailService emailService;
    private final GroupsApi groupsApi;
    private final GroupRepository groupRepository;
    private final BillRepository billRepository;
    private final UTCDateService dateService;

    @Inject
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           CurrentUserContext currentUserContext,
                           EmailService emailService,
                           GroupsApi groupsApi,
                           GroupRepository groupRepository,
                           BillRepository billRepository,
                           UTCDateService dateService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.currentUserContext = currentUserContext;
        this.emailService = emailService;
        this.groupsApi = groupsApi;
        this.groupRepository = groupRepository;
        this.billRepository = billRepository;
        this.dateService = dateService;
    }

    @Override
    public Pair<String, User> createUser(UserDTO userDTO) {
        User user = new User();
        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            // Email already registered! Return error status
            // with empty user object because client handling is problematic else
            return new Pair<>("InvalidEmail", null);
        }
        String emailVerificationCode = UUID.randomUUID().toString();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRegistrationDate(dateService.getConvertedUTCDate());
        user.setEmailVerified(false); //verify Email to activate account
        user.setEmailVerificationCode(emailVerificationCode);
        User savedUser = userRepository.save(user);
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("username", userDTO.getUsername());
        templateModel.put("verificationLink",
                PARTIDO_API_BASE + USERS_API_PATH + savedUser.getId() + VERIFY_PATH + emailVerificationCode);
        emailService.sendEmailVerificationMail(userDTO.getEmail(), templateModel);
        return new Pair<>(null, savedUser);
    }

    @Override
    public Pair<String, User> deleteUser(Long userId) {

        User foundUser = userRepository.findById(userId).orElse(null);
        if (foundUser == null) {
            return new Pair<>("NoSuchUserExists", null);
        }

        List<Group> groups = groupsApi.getCurrentUsersGroups();

        // Make sure that all groups where user is member
        // of have been settled up before deleting user
        for (Group group : groups) {
            Report report = groupsApi.getGroupReport(group.getId());
            for (Balance balance : report.getBalances()) {
                if (!(balance.getBalance().compareTo(BigDecimal.ZERO) == 0)) {
                    return new Pair<>("UserHasDebt", foundUser);
                }
            }
        }


        // Remove user from all groups
        for (Group group : groups) {
            List<User> users = group.getUsers();
            List<User> updatedUsers = new ArrayList<>();
            for (User user: users) {
                if (!user.getId().equals(userId)) {
                    updatedUsers.add(user);
                }
            }
            group.setUsers(updatedUsers);
            groupRepository.save(group);
        }

        userRepository.delete(foundUser);
        // User is found, return user to API.
        return new Pair<>(null, foundUser);
    }

    @Override
    public Pair<String, User> updateUser(Long userId, UserDTO userDTO) {

        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.get() == null) {
            return new Pair<>("UserNotFound", null);
        }

        boolean userEmailChanged = userOptional.isPresent() && !userOptional.get().getEmail().equals(userDTO.getEmail());

        // Check if user wants to change it's current email address and if the new one
        // is already registered. Return an error status code if it is.
        if (userEmailChanged && userRepository.findByEmail(userDTO.getEmail()) != null) {
            return new Pair<>("UpdatedEmail", userOptional.get());
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

            User updatedUser = userOptional.get();

            updatedUser.setUsername(userDTO.getUsername());
            updatedUser.setEmail(userDTO.getEmail());

            // If the email address has been changed, change verification details in user's entity
            if (userEmailChanged) {
                updatedUser.setEmailVerificationCode(emailVerificationCode);
                updatedUser.setEmailVerified(false);
            }

            // If a new password has been provided and it is valid, save it
            if (userDTO.getNewPassword() != null && userDTO.getNewPassword().length() > 6) {
                updatedUser.setPassword(passwordEncoder.encode(userDTO.getNewPassword()));
            }
            return new Pair<>(null, userRepository.save(updatedUser));
        }

        return new Pair<>("NothingUpdated", userOptional.get());
    }
}
