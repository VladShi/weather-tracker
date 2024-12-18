package ru.vladshi.springlearning.services;

import ru.vladshi.springlearning.entities.User;

import java.util.Optional;

public interface UserManagementService {

    void logIn(User user);

    void register(User user);

    Optional<User> authenticate(String sessionId);
}
