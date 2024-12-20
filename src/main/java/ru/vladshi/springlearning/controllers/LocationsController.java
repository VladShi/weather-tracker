package ru.vladshi.springlearning.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.vladshi.springlearning.Validators.LocationNameValidator;
import ru.vladshi.springlearning.dto.LocationDto;
import ru.vladshi.springlearning.entities.User;
import ru.vladshi.springlearning.mappers.DtoMapper;
import ru.vladshi.springlearning.services.LocationService;
import ru.vladshi.springlearning.services.UserManagementService;
import ru.vladshi.springlearning.services.WeatherApiService;

import java.util.List;
import java.util.Optional;

import static ru.vladshi.springlearning.constants.ModelAttributeConstants.*;
import static ru.vladshi.springlearning.constants.RouteConstants.*;
import static ru.vladshi.springlearning.constants.ViewConstants.LOCATIONS_VIEW;

@Controller
@RequiredArgsConstructor
public class LocationsController extends BaseController {

    @Value("${limit.user-locations}")
    private int userLocationsLimit;

    private final UserManagementService userManagementService;
    private final WeatherApiService weatherApiService;
    private final LocationService locationService;

    @GetMapping(LOCATIONS_ROUTE)
    public String searchLocation(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                                 @RequestParam(value = "location-name", required = false) String locationName,
                                 Model model) {

        Optional<User> userOptional = userManagementService.authenticate(sessionId);
        userOptional.ifPresent(user -> model.addAttribute(USER_ATTRIBUTE, user));

        if (hasUserReachedLocationsLimit(userOptional)) {
            model.addAttribute(ERROR_MESSAGE_ATTRIBUTE,
                    "The number of locations to save is limited. Delete any location before proceeding");
            return LOCATIONS_VIEW;
        }

        String locationNameError = LocationNameValidator.validate(locationName);
        if (locationNameError.isEmpty()) {

            List<LocationDto> locations = weatherApiService.getLocationsByName(locationName);

            if (locations.isEmpty()) {
                locationNameError = "No locations with the specified name were found";
            } else {
                model.addAttribute(LOCATIONS_ATTRIBUTE, locations);
            }
        }

        model.addAttribute(ERROR_MESSAGE_ATTRIBUTE, locationNameError);
        model.addAttribute(LOCATION_NAME_ATTRIBUTE, locationName);

        return LOCATIONS_VIEW; // TODO подумать о том что бы все константы передавать во view
    }

    @PostMapping(LOCATIONS_ROUTE)
    public String saveLocationForUser(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                                      @ModelAttribute(LOCATION_ATTRIBUTE) LocationDto locationDto) {

        Optional<User> userOptional = userManagementService.authenticate(sessionId);
        if (userOptional.isEmpty()) {
            return REDIRECT_LOGIN;
        }

        if (!hasUserReachedLocationsLimit(userOptional)) {
            locationService.addLocationToUser(userOptional.get(), DtoMapper.toEntity(locationDto));
        }

        return REDIRECT_INDEX_PAGE;
    }

    @PostMapping(REMOVE_LOCATION_ROUTE)
    public String removeLocationFromUser(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                                         @RequestParam("location-id") int locationId) {

        Optional<User> userOptional = userManagementService.authenticate(sessionId);

        userOptional.ifPresent(user -> locationService.removeLocationFromUser(user, locationId));

        return REDIRECT_INDEX_PAGE;
    }

    private boolean hasUserReachedLocationsLimit(Optional<User> userOptional) {
        return userOptional.map(user -> user.getLocations().size() >= userLocationsLimit).orElse(false);
    }
}
