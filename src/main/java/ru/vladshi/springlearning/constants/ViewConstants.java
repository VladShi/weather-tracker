package ru.vladshi.springlearning.constants;

public class ViewConstants {

    public static final String INDEX_PAGE_VIEW = "index";
    public static final String LOGIN_VIEW = "sign-in-with-errors";
    public static final String REGISTER_VIEW = "sign-up-with-errors";
    public static final String LOCATIONS_VIEW = "search-results";

    public static final String ERROR_500_VIEW = "error";

    private ViewConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
