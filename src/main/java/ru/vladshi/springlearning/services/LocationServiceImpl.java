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
import ru.vladshi.springlearning.mappers.DtoMapper;
import ru.vladshi.springlearning.exceptions.UnexpectedLocationException;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        userDao.merge(user); // TODO ограничить максимальное количество локаций для юзера. Проверяем если длина списка локаций больше константы делаем ретёрн. Во контроллере тоже проверяем что если длина списка локаций больше константы то добавляем ошибку с текстом что слишком много локаций, лимит превышен, купите премиум подписку
    }

    @Override
    public void removeLocationFromUser(User user, int locationId) {
        userLocationDao.deleteLocationFromUser(user.getId(), locationId);
    }

    private void validateLocationByWeatherApi(Location requiredLocation) {
        List<LocationDto> locationDtos = weatherApiService.getLocationsByName(requiredLocation.getName());
        List<Location> locations = locationDtos.stream().map(DtoMapper::toEntity).toList();
        Optional<Location> foundLocation = getCloseCoordinatesLocation(locations, requiredLocation);

        if (foundLocation.isEmpty()) {
            throw new UnexpectedLocationException( // TODO добавить обработку ошибки в глобальный перехватчик
                    "An attempt to save a location for the user " +
                            "that does not match the locations of the external weather api.");
        }
    }

    private Optional<Location> getCloseCoordinatesLocation(List<Location> foundLocations,Location requiredLocation) {
        for (Location foundLocation : foundLocations) {
            if (haveCloseCoordinates(requiredLocation, foundLocation)) {
                return Optional.of(foundLocation);
            }
        }
        return Optional.empty();
    }

    private void updateIdOrSave(Location location) {
        List<Location> foundLocations = locationDao.findByName(location.getName());
        Optional<Location> foundLocation = getCloseCoordinatesLocation(foundLocations, location);

        if (foundLocation.isPresent()) {
            location.setId(foundLocation.get().getId());
        } else {
            locationDao.save(location);
        }
    }

    private boolean haveCloseCoordinates(Location requiredLocation, Location existingLocation) {
        // This method is necessary because on the external weather api, some locations have slightly different
        // coordinates when requested in different languages. Although in fact it is the same location.
        var requiredLongitude = requiredLocation.getLongitude().setScale(3, RoundingMode.HALF_UP);
        var requiredLatitude = requiredLocation.getLatitude().setScale(3, RoundingMode.HALF_UP);

        var foundLongitude = existingLocation.getLongitude().setScale(3, RoundingMode.HALF_UP);
        var foundLatitude = existingLocation.getLatitude().setScale(3, RoundingMode.HALF_UP);

        return requiredLatitude.equals(foundLatitude) || requiredLongitude.equals(foundLongitude);
    }

    private void addLocationToUserLocationsList(Location location, User user) {
        if (user.getLocations() == null) {
            user.setLocations(new ArrayList<>());
        }
        if (!hasUserLocation(user, location)) {
            user.getLocations().add(location);
        }
    }

    private boolean hasUserLocation(User user, Location requiredLocation) {
        var userLocations = user.getLocations();
        return userLocations.stream().anyMatch(userLocation -> userLocation.getId() == requiredLocation.getId());
    }
}
