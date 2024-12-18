package ru.vladshi.springlearning.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladshi.springlearning.dao.LocationDao;
import ru.vladshi.springlearning.dao.UserDao;
import ru.vladshi.springlearning.dao.UserLocationDao;
import ru.vladshi.springlearning.entities.Location;
import ru.vladshi.springlearning.entities.User;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationDao locationDao;
    private final UserDao userDao;
    private final UserLocationDao userLocationDao;

    @Override
    public void addLocationToUser(User user, Location location) {
        updateIdOrSave(location);
        addLocationToUserLocationsList(location, user);
        userDao.merge(user); // TODO ограничить максимальное количество локаций для юзера
    }

    @Override
    public void removeLocationFromUser(User user, int locationId) {
        userLocationDao.deleteLocationFromUser(user.getId(), locationId);
    }

    private void addLocationToUserLocationsList(Location location, User user) {
        if (user.getLocations() == null) {
            user.setLocations(new ArrayList<>());
        }
        if (!user.getLocations().contains(location)) {
            user.getLocations().add(location);
        }
    }

    private void updateIdOrSave(Location location) {
        Optional<Location> existingLocation = locationDao.find(location);

        if (existingLocation.isPresent()) {
            location.setId(existingLocation.get().getId());
        } else {
            locationDao.save(location);
        }
    }
}
