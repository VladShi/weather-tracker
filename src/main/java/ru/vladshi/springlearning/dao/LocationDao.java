package ru.vladshi.springlearning.dao;

import ru.vladshi.springlearning.entities.Location;

import java.util.List;

public interface LocationDao {

    void save(Location location);

    List<Location> findByName(String name);

    void deleteById(int locationId);
}
