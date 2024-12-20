package ru.vladshi.springlearning.dao;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserLocationDaoImpl implements UserLocationDao {

    private final SessionFactory sessionFactory;

    @Override
    public void deleteLocationFromUser(int userId, int locationId) {
        Session session = sessionFactory.getCurrentSession();

        String sql = "DELETE FROM user_location WHERE user_id = :userId AND location_id = :locationId";
        session.createNativeMutationQuery(sql)
            .setParameter("userId", userId)
            .setParameter("locationId", locationId)
            .executeUpdate();
    }

    @Override
    public Boolean checkIsLocationInUse(int locationId) {
        Session session = sessionFactory.getCurrentSession();
        String checkSql = "SELECT EXISTS (SELECT 1 FROM user_location WHERE location_id = :locationId)";

        return session.createNativeQuery(checkSql, Boolean.class)
                .setParameter("locationId", locationId)
                .getSingleResult();
    }
}
