package com.jeancoder.jdbc.bean;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD, ElementType.METHOD})
@Retention(RUNTIME)
public @interface JCForeign {

}
