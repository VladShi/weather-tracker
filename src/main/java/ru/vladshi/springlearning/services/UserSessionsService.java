package ru.vladshi.springlearning.services;

import ru.vladshi.springlearning.entities.User;
import ru.vladshi.springlearning.entities.UserSession;

import java.util.Optional;

public interface UserSessionsService {

    Optional<UserSession> getUserSession(String sessionId);

    UserSession findOrCreate(User user);
}
