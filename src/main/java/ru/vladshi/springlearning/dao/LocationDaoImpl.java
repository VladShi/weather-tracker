package ru.vladshi.springlearning.dao;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;
import ru.vladshi.springlearning.entities.Location;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LocationDaoImpl implements LocationDao {

    private final SessionFactory sessionFactory;

    @Override
    public void save(Location location) {
        sessionFactory.getCurrentSession().persist(location);
    }

    @Override
    public List<Location> findByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "from Location where name = :name";

        return session.createQuery(hql, Location.class)
                .setParameter("name", name)
                .list();
    }
}
