package ru.vladshi.springlearning.dao;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
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

        if (!checkIsLocationInUse(locationId, session)) {
            deleteLocationById(locationId, session);
        }
    }

    private static Boolean checkIsLocationInUse(int locationId, Session session) {
        String checkSql = "SELECT EXISTS (SELECT 1 FROM user_location WHERE location_id = :locationId)";
        NativeQuery<Boolean> checkQuery = session.createNativeQuery(checkSql, Boolean.class);
        checkQuery.setParameter("locationId", locationId);
        return checkQuery.getSingleResult();
    }

    private static void deleteLocationById(int locationId, Session session) {
        String deleteSql = "DELETE FROM location WHERE id = :locationId";
        session.createNativeMutationQuery(deleteSql)
            .setParameter("locationId", locationId)
            .executeUpdate();
    }
}
