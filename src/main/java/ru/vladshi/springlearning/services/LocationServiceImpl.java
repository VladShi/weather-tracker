package ru.vladshi.springlearning.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladshi.springlearning.dao.LocationDao;
import ru.vladshi.springlearning.dao.UserDao;
import ru.vladshi.springlearning.dao.UserLocationDao;
import ru.vladshi.springlearning.dto.LocationDto;
import ru.vladshi.springlearning.entities.Location;
import ru.vladshi.springlearning.entities.User;
import ru.vladshi.springlearning.exceptions.UnexpectedLocationException;
import ru.vladshi.springlearning.mappers.DtoMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationDao locationDao;
    private final UserDao userDao;
    private final UserLocationDao userLocationDao;
    private final WeatherApiService weatherApiService;

    @Override
    public void addLocationToUser(User user, Location location) {
        validateLocationByWeatherApi(location);
        updateIdOrSave(location);
        addLocationToUserLocationsList(location, user);
        userDao.merge(user);
    }

    @Override
    public void removeLocationFromUser(User user, int locationId) {
        userLocationDao.deleteLocationFromUser(user.getId(), locationId);
    }

    private void validateLocationByWeatherApi(Location requiredLocation) {
        List<LocationDto> locationDtos = weatherApiService.getLocationsByName(requiredLocation.getName());
        List<Location> locations = locationDtos.stream().map(DtoMapper::toEntity).toList();

        if (!locations.contains(requiredLocation)) {
            throw new UnexpectedLocationException(
                    "An attempt to save a location for the user " +
                            "that does not match the locations of the external weather api.");
        }
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
        List<Location> foundLocations = locationDao.findByName(location.getName());
        boolean isLocationAlreadyExists = false;
        for (Location foundLocation : foundLocations) {
            if (foundLocation.equals(location)) {
                location.setId(foundLocation.getId());
                isLocationAlreadyExists = true;
            }
        }
        if (!isLocationAlreadyExists) {
            locationDao.save(location);
        }
    }
}
