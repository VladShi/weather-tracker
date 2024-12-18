package ru.vladshi.springlearning.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.vladshi.springlearning.dto.WeatherDto;
import ru.vladshi.springlearning.entities.User;
import ru.vladshi.springlearning.services.UserManagementService;
import ru.vladshi.springlearning.services.WeatherApiService;

import java.util.List;
import java.util.Optional;

import static ru.vladshi.springlearning.constants.ModelAttributeConstants.USER_ATTRIBUTE;
import static ru.vladshi.springlearning.constants.ModelAttributeConstants.WEATHERS_LIST_ATTRIBUTE;
import static ru.vladshi.springlearning.constants.RouteConstants.*;
import static ru.vladshi.springlearning.constants.ViewConstants.*;

@Controller
@RequiredArgsConstructor
@RequestMapping(INDEX_PAGE_ROUTE)
public class IndexPageController extends BaseController {

    private final UserManagementService userManagementService;
    private final WeatherApiService weatherApiService;

    @GetMapping()
    public String index(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                        Model model) {

        Optional<User> userOptional = userManagementService.authenticate(sessionId);

        if (userOptional.isPresent()) {
            List<WeatherDto> weathers = weatherApiService.getWeathers(userOptional.get().getLocations());
            model.addAttribute(WEATHERS_LIST_ATTRIBUTE, weathers);
            model.addAttribute(USER_ATTRIBUTE, userOptional.get());
        }

        return INDEX_PAGE_VIEW;
    }
}