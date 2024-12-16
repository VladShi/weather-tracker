package ru.vladshi.springlearning.constants;

public class ViewConstants {

    public static final String INDEX_PAGE_VIEW = "index";
    public static final String LOGIN_VIEW = "login";
    public static final String REGISTER_VIEW = "register";
    public static final String LOCATIONS_VIEW = "locations";

    private ViewConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
