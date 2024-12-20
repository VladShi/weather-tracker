package ru.vladshi.springlearning.dao;

public interface UserLocationDao {

    void deleteLocationFromUser(int userId, int locationId);

    Boolean checkIsLocationInUse(int locationId);
}
