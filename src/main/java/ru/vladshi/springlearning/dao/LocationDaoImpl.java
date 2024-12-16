package ru.vladshi.springlearning.dao;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;
import ru.vladshi.springlearning.entities.Location;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LocationDaoImpl implements LocationDao {

    private final SessionFactory sessionFactory;

    @Override
    public void save(Location location) {
        sessionFactory.getCurrentSession().persist(location);
    }

    @Override
    public Optional<Location> find(Location location) {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery("from Location where latitude = :lat and longitude = :lon", Location.class)
                .setParameter("lat", location.getLatitude())
                .setParameter("lon", location.getLongitude())
                .uniqueResultOptional();
    }
}
