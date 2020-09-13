package net.fosforito.partido.model.group;

import net.fosforito.partido.api.GroupsApi;
import net.fosforito.partido.database.UTCDateService;
import net.fosforito.partido.mail.EmailService;
import net.fosforito.partido.model.bill.BillRepository;
import net.fosforito.partido.model.user.*;
import net.fosforito.partido.util.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.doAnswer;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {


    UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CurrentUserContext currentUserContext;
    @Mock
    private EmailService emailService;
    @Mock
    private GroupsApi groupsApi;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private BillRepository billRepository;
    @Mock
    private UTCDateService dateService;

    @Before
    public void init() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, currentUserContext, emailService, groupsApi, groupRepository, billRepository, dateService);
        doAnswer(returnsFirstArg()).when(userRepository).save(Mockito.any(User.class));
    }

    @Test
    public void testRegisterDuplicateEmail() {
        User userWithEmail = new User("username", "user@email.com", "password", new Date(), true, "");

        Mockito.when(userRepository.findByEmail("user@email.com"))
                .thenReturn(userWithEmail);

        Pair<String, User> validationPair = userService.createUser(new UserDTO("username", "user@email.com", "password", null));
        assertNotNull(validationPair.getFirst());
        assertNull(validationPair.getSecond());
        assertEquals("InvalidEmail", validationPair.getFirst());
    }

    @Test
    public void testRegisterSuccessfulUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setEmail("user@email.com");
        user.setPassword("password");
        user.setEmailVerified(false);
        Mockito.when(userRepository.findByEmail("user@email.com"))
                .thenReturn(null);

        Pair<String, User> validationPair = userService.createUser(new UserDTO("username", "user@email.com", "password", null));
        assertNull(validationPair.getFirst());
        assertNotNull(validationPair.getSecond());
        assertEquals("username", validationPair.getSecond().getUsername());
        assertEquals("user@email.com", validationPair.getSecond().getEmail());
    }

    @Test
    public void testDeleteNonExistantUser() {
        Pair<String, User> validationPair = userService.deleteUser(1L);
        assertNotNull(validationPair.getFirst());
        assertNull(validationPair.getSecond());
        assertEquals("NoSuchUserExists", validationPair.getFirst());
    }
}
