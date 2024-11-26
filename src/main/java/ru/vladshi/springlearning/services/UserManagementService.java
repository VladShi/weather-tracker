package ru.vladshi.springlearning.services;

import ru.vladshi.springlearning.entities.User;


public interface UserManagementService {

    String logIn(User user);

    String register(User user);
}
