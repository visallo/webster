package org.visallo.webster.annotations;

import org.visallo.webster.DefaultParameterValueConverter;
import org.visallo.webster.ParameterValueConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Optional {
    String NOT_SET = "--not-set--";

    String name();

    String defaultValue() default NOT_SET;

    boolean allowEmpty() default true;

    Class<? extends ParameterValueConverter> parameterValueConverter() default DefaultParameterValueConverter.class;
}
