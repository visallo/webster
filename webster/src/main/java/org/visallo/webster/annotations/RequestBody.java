package org.visallo.webster.annotations;

import org.visallo.webster.DefaultRequestBodyValueConverter;
import org.visallo.webster.RequestBodyValueConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestBody {
    Class<? extends RequestBodyValueConverter> requestBodyValueConverter() default DefaultRequestBodyValueConverter.class;
}
