package ru.vladshi.springlearning.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.vladshi.springlearning.dto.LocationDto;
import ru.vladshi.springlearning.entities.User;
import ru.vladshi.springlearning.mappers.DtoMapper;
import ru.vladshi.springlearning.services.LocationService;
import ru.vladshi.springlearning.services.UserManagementService;
import ru.vladshi.springlearning.services.WeatherApiService;

import static ru.vladshi.springlearning.constants.ModelAttributeConstants.*;
import static ru.vladshi.springlearning.constants.RouteConstants.*;
import static ru.vladshi.springlearning.constants.ViewConstants.LOCATIONS_VIEW;

@Controller
@RequiredArgsConstructor
public class LocationsController extends BaseController {

    private final UserManagementService userManagementService;
    private final WeatherApiService weatherApiService;
    private final LocationService locationService;

    @GetMapping(LOCATIONS_ROUTE)
    public String searchLocation(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                                 @RequestParam("location-name") String locationName,
                                 Model model) {

        User authUser = userManagementService.authenticate(sessionId);  // TODO добавить валидацию для locationName

        model.addAttribute(USER_ATTRIBUTE, authUser);
        model.addAttribute(LOCATIONS_ATTRIBUTE, weatherApiService.getLocationsByName(locationName));

        return LOCATIONS_VIEW; // TODO подумать о том что бы все константы передавать во view
    }

    @PostMapping(LOCATIONS_ROUTE)
    public String saveLocationForUser(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                                      @ModelAttribute(LOCATION_ATTRIBUTE) LocationDto locationDto) {

        User authUser = userManagementService.authenticate(sessionId);

        locationService.addLocationToUser(authUser, DtoMapper.toEntity(locationDto));

        return REDIRECT_INDEX_PAGE;
    }

    @PostMapping(REMOVE_LOCATION_ROUTE)
    public String removeLocationFromUser(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                                         @RequestParam("location-id") int locationId) {

        User authUser = userManagementService.authenticate(sessionId);

        locationService.removeLocationFromUser(authUser, locationId);

        return REDIRECT_INDEX_PAGE;
    }
}
