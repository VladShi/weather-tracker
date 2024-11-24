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
import ru.vladshi.springlearning.dao.UserDaoImpl;
import ru.vladshi.springlearning.entities.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {WebConfig.class, UserManagementServiceImpl.class, UserDaoImpl.class})
@TestPropertySource("classpath:application-test.properties")
@WebAppConfiguration
@Transactional
class UserManagementServiceImplTest {

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private SessionFactory sessionFactory;

    @BeforeEach
    public void setUp() {
        Session session = sessionFactory.getCurrentSession();
        session.createMutationQuery("DELETE FROM User").executeUpdate();

        User testUser = new User();
        testUser.setLogin("testUser");
        testUser.setPassword("testPassword");
        session.persist(testUser);
    }

    @Test
    public void testRegisterUser() {
        User newUser = new User();
        String login = "newUser";
        String password = "newPassword";
        newUser.setLogin(login);
        newUser.setPassword(password);

        userManagementService.register(newUser);

        Session session = sessionFactory.getCurrentSession();
        User user = session.createQuery("FROM User WHERE login = :login", User.class)
                .setParameter("login", login)
                .uniqueResult();

        assertNotNull(user);
        assertEquals(login, user.getLogin());
        assertTrue(checkPasswords(password, user.getPassword()));
    }

    private boolean checkPasswords(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}