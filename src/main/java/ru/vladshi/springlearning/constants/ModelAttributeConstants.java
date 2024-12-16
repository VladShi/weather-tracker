package ru.vladshi.springlearning.constants;

public final class ModelAttributeConstants {

    public static final String USER_ATTRIBUTE = "user";
    public static final String LOCATION_ATTRIBUTE = "location";
    public static final String LOCATIONS_ATTRIBUTE = "locations";
    public static final String WEATHERS_LIST_ATTRIBUTE = "weathersForUser";
    public static final String ERROR_MESSAGE_ATTRIBUTE = "errorMessage";

    private ModelAttributeConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
