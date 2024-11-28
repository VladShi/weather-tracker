package ru.vladshi.springlearning.services;

import ru.vladshi.springlearning.entities.User;
import ru.vladshi.springlearning.entities.UserSession;

import java.util.Optional;

public interface UserSessionsService {

    Optional<UserSession> getById(String sessionId);

    UserSession findOrCreate(User user);

    void updateLastActivity(UserSession session);
}
