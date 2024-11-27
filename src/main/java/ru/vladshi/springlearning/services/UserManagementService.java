package ru.vladshi.springlearning.services;

import ru.vladshi.springlearning.entities.User;


public interface UserManagementService {

    void logIn(User user);

    void register(User user);

    void authentificate(User user, String sessionId);
}
