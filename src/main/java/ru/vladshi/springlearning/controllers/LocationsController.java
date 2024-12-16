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
                                 @ModelAttribute(USER_ATTRIBUTE) User user,
                                 Model model) {

        userManagementService.authenticate(user, sessionId); // TODO подумать сделать так что бы authenticate возвращал user, вместо того что бы принимать и внутри копировать поля

        model.addAttribute(LOCATIONS_ATTRIBUTE, weatherApiService.getLocationsByName(locationName));

        return LOCATIONS_VIEW; // TODO подумать о том что бы все константы передавать во view
    }

    @PostMapping(LOCATIONS_ROUTE)
    public String saveLocationForUser(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                                      @ModelAttribute(USER_ATTRIBUTE) User user,
                                      @ModelAttribute(LOCATION_ATTRIBUTE) LocationDto locationDto) {

        userManagementService.authenticate(user, sessionId);

        locationService.addLocationToUser(user, DtoMapper.toEntity(locationDto));

        return REDIRECT_INDEX_PAGE;
    }

    @PostMapping(REMOVE_LOCATION_ROUTE)
    public String removeLocationFromUser(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                                         @RequestParam("location-id") int locationId,
                                         @ModelAttribute(USER_ATTRIBUTE) User user) {

        userManagementService.authenticate(user, sessionId);

        locationService.removeLocationFromUser(user, locationId);

        return REDIRECT_INDEX_PAGE;
    }
}
