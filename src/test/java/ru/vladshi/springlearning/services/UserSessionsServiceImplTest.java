package ru.vladshi.springlearning.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.vladshi.springlearning.config.WebConfig;
import ru.vladshi.springlearning.dao.UserDao;
import ru.vladshi.springlearning.entities.User;
import ru.vladshi.springlearning.entities.UserSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {WebConfig.class})
@TestPropertySource("classpath:application-test.properties")
@WebAppConfiguration
@Transactional
class UserSessionsServiceImplTest {

    private static final String TEST_LOGIN = "testuser";
    private static final String TEST_PASSWORD = "testpassword";

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private UserSessionsService userSessionsService;

    @Autowired
    private UserDao userDao;

    private User testUser;

    @BeforeEach
    public void setUp() {
        Session session = sessionFactory.getCurrentSession();
        session.createMutationQuery("DELETE FROM UserSession").executeUpdate();
        session.createMutationQuery("DELETE FROM User").executeUpdate();

        // Создаем тестового пользователя
        testUser = new User();
        testUser.setLogin(TEST_LOGIN);
        testUser.setPassword(TEST_PASSWORD);
        userDao.save(testUser);
    }

    @Test
    public void testFindOrCreate_createsNewSession() {
        // Given
        // Пользователь уже создан в setUp()

        // When
        UserSession userSession = userSessionsService.findOrCreate(testUser);

        // Then
        assertNotNull(userSession, "Session must be created");
        assertEquals(testUser, userSession.getUser(), "Session must be associated with the user");
        assertNotNull(userSession.getExpiresAt(), "Session expiration time must be set");

        // Дополнительная проверка в базе данных
        Session session = sessionFactory.getCurrentSession();
        List<UserSession> sessions = session.createQuery(
                        "FROM UserSession WHERE user.login = :login", UserSession.class)
                .setParameter("login", TEST_LOGIN)
                .getResultList();

        assertEquals(1, sessions.size(), "Only one session should exist in the database");
    }

    @Test
    public void testFindOrCreate_returnsExistingActiveSession() {
        // Given
        UserSession firstSession = userSessionsService.findOrCreate(testUser);

        // When
        UserSession secondSession = userSessionsService.findOrCreate(testUser);

        // Then
        assertEquals(firstSession.getId(), secondSession.getId(),
                "Should return the same active session");
    }

    @Test
    public void testFindOrCreate_replacesExpiredSession() {
        // Given
        Session session = sessionFactory.getCurrentSession();
        UserSession expiredSession = new UserSession();
        expiredSession.setUser(testUser);
        expiredSession.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        session.persist(expiredSession);
        session.flush();

        // When
        UserSession newSession = userSessionsService.findOrCreate(testUser);

        // Then
        assertNotNull(newSession, "New session must be created");
        assertNotEquals(expiredSession.getId(), newSession.getId(),
                "New session must be different from the expired session");

        // Проверка удаления просроченной сессии
        List<UserSession> sessions = session.createQuery(
                        "FROM UserSession WHERE user.login = :login", UserSession.class)
                .setParameter("login", TEST_LOGIN)
                .getResultList();

        assertEquals(1, sessions.size(), "Only new session should remain");
        assertEquals(newSession.getId(), sessions.get(0).getId(),
                "New session must be the only one in the database");
    }

    @Test
    public void testGetById_returnsActiveSession() {
        // Given
        UserSession userSession = userSessionsService.findOrCreate(testUser);

        // When
        Optional<UserSession> foundSession = userSessionsService.getById(userSession.getId());

        // Then
        assertTrue(foundSession.isPresent(), "Active session must be found");
        assertEquals(userSession.getId(), foundSession.get().getId(),
                "Found session must match the created session");
    }

    @Test
    public void testGetById_ignoresExpiredSession() {
        // Given
        Session session = sessionFactory.getCurrentSession();
        UserSession expiredSession = new UserSession();
        expiredSession.setUser(testUser);
        expiredSession.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        session.persist(expiredSession);
        session.flush();

        // When
        Optional<UserSession> foundSession = userSessionsService.getById(expiredSession.getId());

        // Then
        assertTrue(foundSession.isEmpty(), "Expired session must not be returned");

        // Проверка удаления просроченной сессии
        List<UserSession> sessions = session.createQuery(
                        "FROM UserSession WHERE id = :id", UserSession.class)
                .setParameter("id", expiredSession.getId())
                .getResultList();

        assertTrue(sessions.isEmpty(), "Expired session must be deleted from the database");
    }

    @Test
    public void testGetById_nonExistentSessionReturnsEmptyOptional() {
        // Given
        String nonExistentSessionId = "non-existent-session-id";

        // When
        Optional<UserSession> foundSession = userSessionsService.getById(nonExistentSessionId);

        // Then
        assertTrue(foundSession.isEmpty(), "Non-existent session must return empty Optional");

        // Дополнительная проверка, что метод не вызывает ошибок при несуществующем ID
        Session session = sessionFactory.getCurrentSession();
        List<UserSession> sessions = session.createQuery(
                        "FROM UserSession WHERE id = :id", UserSession.class)
                .setParameter("id", nonExistentSessionId)
                .getResultList();

        assertTrue(sessions.isEmpty(), "No sessions should exist with the given non-existent ID");
    }
}

