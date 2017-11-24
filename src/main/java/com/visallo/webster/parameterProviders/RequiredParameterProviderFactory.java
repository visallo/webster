package com.visallo.webster.parameterProviders;

import com.visallo.webster.ParameterValueConverter;
import com.visallo.webster.WebsterException;
import com.visallo.webster.annotations.Required;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class RequiredParameterProviderFactory<T> extends ValueParameterProviderFactory<T> {
    @Override
    public boolean isHandled(Method handleMethod, Class<? extends T> parameterType, Annotation[] parameterAnnotations) {
        return getRequiredAnnotation(parameterAnnotations) != null;
    }

    @Override
    public ParameterProvider<T> createParameterProvider(Method handleMethod, Class<?> parameterType, Annotation[] parameterAnnotations) {
        Required requiredAnnotation = getRequiredAnnotation(parameterAnnotations);
        if (requiredAnnotation == null) {
            throw new WebsterException("Could not find required annotation");
        }
        ParameterValueConverter parameterValueConverter = createParameterValueConverter(requiredAnnotation.parameterValueConverter());
        return new RequiredParameterProvider<>(parameterType, requiredAnnotation, parameterValueConverter);
    }

    private static Required getRequiredAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Required) {
                return (Required) annotation;
            }
        }
        return null;
    }
}
