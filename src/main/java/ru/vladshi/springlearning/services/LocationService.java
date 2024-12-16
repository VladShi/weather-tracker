package ru.vladshi.springlearning.services;

import ru.vladshi.springlearning.entities.Location;
import ru.vladshi.springlearning.entities.User;

public interface LocationService {

    void addLocationToUser(User user, Location location);

    void removeLocationFromUser(User user, int locationId);
}
