package ru.vladshi.springlearning.dao;

import ru.vladshi.springlearning.entities.User;

import java.util.Optional;

public interface UserDao {
    Optional<User> findByLogin(String login);

    void save(User user);

    void merge(User user);
}
