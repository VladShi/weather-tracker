package ru.vladshi.springlearning.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladshi.springlearning.dao.UserSessionDao;
import ru.vladshi.springlearning.entities.User;
import ru.vladshi.springlearning.entities.UserSession;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UserSessionsServiceImpl implements UserSessionsService {

    private final UserSessionDao userSessionDao;
    private final int sessionExpirationMinutes;

    @Autowired
    public UserSessionsServiceImpl(UserSessionDao userSessionsDao,
                                   @Value("${session.expiration.minutes:30}") int sessionExpirationMinutes) {
        this.userSessionDao = userSessionsDao;
        this.sessionExpirationMinutes = sessionExpirationMinutes;
    }

    @Override
    public Optional<UserSession> getById(String sessionId) {

        if (sessionId == null || sessionId.isEmpty()) {
            return Optional.empty();
        }

        Optional<UserSession> userSessionOptional = userSessionDao.findById(sessionId);

        if (userSessionOptional.isPresent()) {

            if (hasSessionExpired(userSessionOptional.get())) {
                userSessionDao.delete(userSessionOptional.get());
                return Optional.empty();
            }
        }
        return userSessionOptional;
    }

    @Override
    public UserSession findOrCreate(User user) {
        Optional<UserSession> userSessionOptional = userSessionDao.findByUser(user);

        if (userSessionOptional.isPresent()) {

            if (hasSessionExpired(userSessionOptional.get())) {
                userSessionDao.delete(userSessionOptional.get());
            } else {
                return userSessionOptional.get();
            }
        }

        UserSession userSession = new UserSession();
        userSession.setUser(user);
        userSession.setExpiresAt(LocalDateTime.now().plusMinutes(sessionExpirationMinutes));
        userSessionDao.save(userSession);

        return userSession;
    }

    @Override
    public void updateLastActivity(UserSession session) {
        session.setLastActivityAt(LocalDateTime.now());
        userSessionDao.save(session);
    }

    private boolean hasSessionExpired(UserSession userSession) {
        LocalDateTime expiresAt = userSession.getExpiresAt();
        return expiresAt.isBefore(LocalDateTime.now());
    }
}
