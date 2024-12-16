package ru.vladshi.springlearning.constants;

public final class RouteConstants {

    public static final String INDEX_PAGE_ROUTE = "/";
    public static final String REGISTER_ROUTE = "/register";
    public static final String LOGIN_ROUTE = "/login";
    public static final String LOGOUT_ROUTE = "/logout";
    public static final String LOCATIONS_ROUTE = "/locations";
    public static final String REMOVE_LOCATION_ROUTE = "/remove-location";

    // Constants for redirecting
    public static final String REDIRECT = "redirect:";
    public static final String REDIRECT_INDEX_PAGE = REDIRECT + INDEX_PAGE_ROUTE;
    public static final String REDIRECT_LOGIN = REDIRECT + LOGIN_ROUTE;


    private RouteConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
