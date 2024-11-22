package ru.vladshi.springlearning.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladshi.springlearning.dao.UserDao;
import ru.vladshi.springlearning.entities.User;
import ru.vladshi.springlearning.entities.UserSession;

import java.util.Optional;

@Service
@Transactional
public class UserManagementServiceImpl implements UserManagementService {

    private final UserDao userDao;
    private final UserSessionsService userSessionsService;

    @Autowired
    public UserManagementServiceImpl(UserDao userDao,
                                     UserSessionsService userSessionsService) {
        this.userDao = userDao;
        this.userSessionsService = userSessionsService;
    }

    @Override
    public String logIn(User requestedUser) {

        Optional<User> existingUserOptional = userDao.findByLogin(requestedUser.getLogin());
        if (existingUserOptional.isEmpty()) {
            return "";
        }
        User existingUser = existingUserOptional.get();

        String hashedPassword = existingUser.getPassword();
        String plainPassword = requestedUser.getPassword();
        if (!checkPasswords(plainPassword, hashedPassword)) {
            return "";
        }

        UserSession userSession = userSessionsService.findOrCreate(existingUser);

        return userSession.getId();
    }

    @Override
    public void logOut(String sessionIdName, HttpServletResponse response) {
        Cookie cookie = new Cookie(sessionIdName, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    @Override
    public void register(User user) {
        hashPassword(user);
        userDao.save(user);
    }

    @Override
    public boolean isLoginTaken(String login) {
        return userDao.findByLogin(login).isPresent();
    }

    private boolean checkPasswords(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    private void hashPassword(User user){
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
    }
}
