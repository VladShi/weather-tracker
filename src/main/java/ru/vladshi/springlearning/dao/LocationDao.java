package ru.vladshi.springlearning.dao;

import ru.vladshi.springlearning.entities.Location;

import java.util.Optional;


public interface LocationDao {

    void save(Location location);

    Optional<Location> find(Location location);
}
