package ru.vladshi.springlearning.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.vladshi.springlearning.config.WebConfig;
import ru.vladshi.springlearning.entities.User;
import ru.vladshi.springlearning.exceptions.InvalidCredentialsException;
import ru.vladshi.springlearning.exceptions.UserAlreadyExistsException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {WebConfig.class})
@TestPropertySource("classpath:application-test.properties")
@WebAppConfiguration
@Transactional
class UserManagementServiceImplTest {

    private static final String TEST_LOGIN = "testuser";
    private static final String TEST_PASSWORD = "testpassword";

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private UserManagementService userManagementService;

    @BeforeEach
    public void setUp() {
        Session session = sessionFactory.getCurrentSession();
        session.createMutationQuery("DELETE FROM UserSession").executeUpdate();
        session.createMutationQuery("DELETE FROM User").executeUpdate();
    }

    private User createTestUser(String login, String password) {
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        return user;
    }

    @Test
    public void testRegisterDuplicateLogin() {
        // Given
        User user1 = createTestUser(TEST_LOGIN, TEST_PASSWORD);
        userManagementService.register(user1);

        // When & Then
        User user2 = createTestUser(TEST_LOGIN, "differentPassword");
        assertThrows(UserAlreadyExistsException.class, () -> userManagementService.register(user2));

        // Additional database check
        Session session = sessionFactory.getCurrentSession();
        List<User> users = session.createQuery(
                        "FROM User WHERE login = :login", User.class)
                .setParameter("login", TEST_LOGIN)
                .getResultList();

        assertEquals(1, users.size(), "Only one user should exist in the database with this login");
    }

    @Test
    public void testPasswordHashing() {
        // Given
        User user = createTestUser(TEST_LOGIN, TEST_PASSWORD);

        // When
        userManagementService.register(user);

        // Then
        Session session = sessionFactory.getCurrentSession();
        User savedUser = session.createQuery(
                        "FROM User WHERE login = :login", User.class)
                .setParameter("login", TEST_LOGIN)
                .uniqueResult();

        assertNotNull(savedUser);
        assertNotEquals(TEST_PASSWORD, savedUser.getPassword(), "Password must be hashed");
        assertTrue(BCrypt.checkpw(TEST_PASSWORD, savedUser.getPassword()), "Password hash must be correct");
    }

    @Test
    public void testLoginWithNonExistentUser() {
        // Given
        User nonExistentUser = createTestUser("nonexistent", "password");

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> userManagementService.logIn(nonExistentUser));

        // Check user absence in the database
        Session session = sessionFactory.getCurrentSession();
        User foundUser = session.createQuery(
                        "FROM User WHERE login = :login", User.class)
                .setParameter("login", "nonexistent")
                .uniqueResult();

        assertNull(foundUser, "User should not exist in the database");
    }

    @Test
    public void testLoginWithWrongPassword() {
        // Given
        User user = createTestUser(TEST_LOGIN, TEST_PASSWORD);
        userManagementService.register(user);

        // When & Then
        User wrongPasswordUser = createTestUser(TEST_LOGIN, "wrongPassword");
        assertThrows(InvalidCredentialsException.class,
                () -> userManagementService.logIn(wrongPasswordUser));

        // Database check
        Session session = sessionFactory.getCurrentSession();
        User savedUser = session.createQuery(
                        "FROM User WHERE login = :login", User.class)
                .setParameter("login", TEST_LOGIN)
                .uniqueResult();

        assertNotNull(savedUser);
        assertFalse(BCrypt.checkpw("wrongPassword", savedUser.getPassword()),
                "Wrong password should not pass authentication");
    }

    @Test
    public void testSuccessfulRegistration() {
        // Given
        User user = createTestUser(TEST_LOGIN, TEST_PASSWORD);

        // When
        userManagementService.register(user);

        // Then
        Session session = sessionFactory.getCurrentSession();
        User savedUser = session.createQuery(
                        "FROM User WHERE login = :login", User.class)
                .setParameter("login", TEST_LOGIN)
                .uniqueResult();

        assertNotNull(savedUser);
        assertEquals(TEST_LOGIN, savedUser.getLogin());
        assertNotEquals(TEST_PASSWORD, savedUser.getPassword(), "Password must be hashed");
    }

    @Test
    public void testSuccessfulLogin() {
        // Given
        User user = createTestUser(TEST_LOGIN, TEST_PASSWORD);

        User userToSave = new User();
        String hashedPassword = BCrypt.hashpw(TEST_PASSWORD, BCrypt.gensalt());
        userToSave.setLogin(TEST_LOGIN);
        userToSave.setPassword(hashedPassword);

        Session session = sessionFactory.getCurrentSession();
        session.persist(userToSave);
        session.flush();

        // When & Then
        assertDoesNotThrow(() -> userManagementService.logIn(user), "User is not logged in");
        assertNotEquals(0, user.getId(), "User id should not be zero, after logging in");
    }
}