package ru.vladshi.springlearning.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.vladshi.springlearning.entities.User;

import java.util.Optional;

@Component
public class UserDaoImpl implements UserDao {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<User> findByLogin(String login) {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery("from User where login = :login", User.class)
                .setParameter("login", login)
                .uniqueResultOptional();
    }

    @Override
    public void save(User user) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(user);
    }
}
