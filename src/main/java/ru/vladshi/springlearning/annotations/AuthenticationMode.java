package ru.vladshi.springlearning.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AuthenticationMode {
    enum Mode {
        AUTHENTICATED,   // Требуется авторизация
        NOT_AUTHENTICATED, // Доступ только неавторизованным
        PUBLIC           // Доступ для всех
    }
    Mode value() default Mode.PUBLIC;
}
