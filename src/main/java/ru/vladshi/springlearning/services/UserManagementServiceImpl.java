package ru.vladshi.springlearning.services;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladshi.springlearning.dao.UserDao;
import ru.vladshi.springlearning.entities.User;
import ru.vladshi.springlearning.entities.UserSession;
import ru.vladshi.springlearning.exceptions.UserAlreadyExistsException;

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
    public String register(User user) {
        if (isLoginTaken(user.getLogin())) {
            throw new UserAlreadyExistsException("User with login " + user.getLogin() + " already exists");
        }

        User userToSave = createUserCopyWithHashedPassword(user);

        userDao.save(userToSave);

        UserSession userSession = userSessionsService.findOrCreate(userToSave);

        return userSession.getId();
    }

    private User createUserCopyWithHashedPassword(User originalUser) {
        User hashedUser = new User();
        hashedUser.setLogin(originalUser.getLogin());
        hashedUser.setLocations(originalUser.getLocations());

        String hashedPassword = BCrypt.hashpw(originalUser.getPassword(), BCrypt.gensalt());
        hashedUser.setPassword(hashedPassword);

        return hashedUser;
    }

    private boolean isLoginTaken(String login) {
        return userDao.findByLogin(login).isPresent();
    }

    private boolean checkPasswords(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
