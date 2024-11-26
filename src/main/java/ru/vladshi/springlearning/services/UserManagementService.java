package ru.vladshi.springlearning.services;

import ru.vladshi.springlearning.entities.User;


public interface UserManagementService {

    String logIn(User user);

    void register(User user);

    boolean isLoginTaken(String login);
}
