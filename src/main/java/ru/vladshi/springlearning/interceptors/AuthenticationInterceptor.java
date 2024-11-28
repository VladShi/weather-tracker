package ru.vladshi.springlearning.interceptors;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import ru.vladshi.springlearning.annotations.AuthenticationMode;
import ru.vladshi.springlearning.constants.RouteConstants;
import ru.vladshi.springlearning.entities.UserSession;
import ru.vladshi.springlearning.services.UserSessionsService;

import java.io.IOException;
import java.util.Optional;

import static ru.vladshi.springlearning.controllers.BaseController.SESSION_COOKIE_NAME;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final UserSessionsService userSessionsService;

    @Autowired
    public AuthenticationInterceptor(UserSessionsService userSessionsService) {
        this.userSessionsService = userSessionsService;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {
        // Проверяем, что это метод контроллера
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        AuthenticationMode.Mode authMode = findAuthenticationMode(handlerMethod);

        // Получаем ID сессии из куки
        String sessionId = extractSessionIdFromCookies(request);

        return switch (authMode) {
            case AUTHENTICATED -> handleAuthenticatedAccess(sessionId, response);
            case NOT_AUTHENTICATED -> handleNotAuthenticatedAccess(sessionId, response);
            default -> handlePublicAccess(sessionId);
        };
    }

    private boolean handleAuthenticatedAccess(String sessionId, HttpServletResponse response) throws IOException {
        Optional<UserSession> session = userSessionsService.getById(sessionId);

        if (session.isEmpty()) {
            response.sendRedirect(RouteConstants.LOGIN_ROUTE);
            return false;
        }

        // Обновляем активность сессии
        userSessionsService.updateLastActivity(session.get());
        return true;
    }

    private boolean handleNotAuthenticatedAccess(String sessionId, HttpServletResponse response) throws IOException {
        Optional<UserSession> session = userSessionsService.getById(sessionId);

        if (session.isPresent()) {
            response.sendRedirect(RouteConstants.INDEX_PAGE_ROUTE);
            return false;
        }

        return true;
    }

    private boolean handlePublicAccess(String sessionId) {
        // Для публичных эндпоинтов просто логируем активность, если сессия есть
        userSessionsService.getById(sessionId)
                .ifPresent(userSessionsService::updateLastActivity);
        return true;
    }

    private AuthenticationMode.Mode findAuthenticationMode(HandlerMethod handlerMethod) {
        // Приоритет: метод -> класс -> дефолтный
        AuthenticationMode methodMode = handlerMethod.getMethod().getAnnotation(AuthenticationMode.class);
        if (methodMode != null) {
            return methodMode.value();
        }

        AuthenticationMode classMode = handlerMethod.getBeanType().getAnnotation(AuthenticationMode.class);
        if (classMode != null) {
            return classMode.value();
        }

        return AuthenticationMode.Mode.PUBLIC;
    }

    private String extractSessionIdFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (SESSION_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}

