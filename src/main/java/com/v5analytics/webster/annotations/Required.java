package com.v5analytics.webster.annotations;

import com.v5analytics.webster.DefaultParameterValueConverter;
import com.v5analytics.webster.ParameterValueConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Required {
    String name();

    boolean allowEmpty() default true;

    Class<? extends ParameterValueConverter> parameterValueConverter() default DefaultParameterValueConverter.class;
}
