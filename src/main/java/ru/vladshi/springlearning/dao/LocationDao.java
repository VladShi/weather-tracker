package ru.vladshi.springlearning.dao;

import ru.vladshi.springlearning.entities.Location;

import java.util.List;


public interface LocationDao {

    void save(Location location);

//    Optional<Location> find(Location location);     // TODO удалить если не нужно

    List<Location> findByName(String name);
}
