package ru.vladshi.springlearning.dao;

import ru.vladshi.springlearning.entities.User;
import ru.vladshi.springlearning.entities.UserSession;

import java.util.Optional;

public interface UserSessionDao {

    Optional<UserSession> findById(String sessionId);

    void delete(UserSession userSession);

    Optional<UserSession> findByUser(User user);

    void save(UserSession userSession);
}
