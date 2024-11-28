package ru.vladshi.springlearning.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.vladshi.springlearning.entities.User;
import ru.vladshi.springlearning.entities.UserSession;

import java.util.Optional;

@Component
public class UserSessionDaoImpl implements UserSessionDao {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserSessionDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<UserSession> findById(String sessionId) {
        Session session = sessionFactory.getCurrentSession();
        return Optional.ofNullable(session.get(UserSession.class, sessionId));
    }

    @Override
    public void delete(UserSession userSession) {
        Session session = sessionFactory.getCurrentSession();
        session.remove(userSession);
        session.flush();
    }

    @Override
    public Optional<UserSession> findByUser(User user) {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery("from UserSession where user = :user", UserSession.class)
                .setParameter("user", user)
                .uniqueResultOptional();
    }

    @Override
    public void save(UserSession userSession) {
        Session session = sessionFactory.getCurrentSession();
        UserSession managedUserSession = session.merge(userSession);
        userSession.setId(managedUserSession.getId());
    }
}
