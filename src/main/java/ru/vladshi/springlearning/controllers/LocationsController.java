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

import java.util.Optional;

import static ru.vladshi.springlearning.Validators.LocationNameValidator.checkIsValid;
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
                                 @RequestParam(value = "location-name", required = false) String locationName,
                                 Model model) {

        Optional<User> userOptional = userManagementService.authenticate(sessionId);

        userOptional.ifPresent(user -> model.addAttribute(USER_ATTRIBUTE, user));

        if(!checkIsValid(locationName)) { // TODO возвращать разный текст ошибок и заменить как-то + и пробелы на - и добавить во вью вывод ошибок валидации , видимо внутри getLocationsByName()
            model.addAttribute(ERROR_MESSAGE_ATTRIBUTE, "The location name must be from 2 to 40"
                    + " english or russian letters and may contain a dash '-' character.");
            return LOCATIONS_VIEW;
        }

        model.addAttribute(LOCATIONS_ATTRIBUTE, weatherApiService.getLocationsByName(locationName));

        return LOCATIONS_VIEW; // TODO подумать о том что бы все константы передавать во view
    }

    @PostMapping(LOCATIONS_ROUTE)
    public String saveLocationForUser(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                                      @ModelAttribute(LOCATION_ATTRIBUTE) LocationDto locationDto) { // TODO проверить какая ошибка вылетает, если прислать пост запрос не с формы сайта, и с неподходящими данными

        Optional<User> userOptional = userManagementService.authenticate(sessionId);

        if (userOptional.isPresent()) {
            locationService.addLocationToUser(userOptional.get(), DtoMapper.toEntity(locationDto));
        } else {
            return REDIRECT_LOGIN;
        }

        return REDIRECT_INDEX_PAGE;
    }

    @PostMapping(REMOVE_LOCATION_ROUTE)
    public String removeLocationFromUser(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                                         @RequestParam("location-id") int locationId) {  // TODO проверить какая ошибка вылетает, если прислать пост запрос не с формы сайта, и с неподходящими данными

        Optional<User> userOptional = userManagementService.authenticate(sessionId);

        userOptional.ifPresent(user -> locationService.removeLocationFromUser(user, locationId));

        return REDIRECT_INDEX_PAGE;
    }
}
