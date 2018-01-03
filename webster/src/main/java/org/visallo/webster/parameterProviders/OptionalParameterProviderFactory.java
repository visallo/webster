package org.visallo.webster.parameterProviders;

import org.visallo.webster.ParameterValueConverter;
import org.visallo.webster.WebsterException;
import org.visallo.webster.annotations.Optional;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class OptionalParameterProviderFactory<T> extends ValueParameterProviderFactory<T> {
    @Override
    public boolean isHandled(Method handleMethod, Class parameterType, Annotation[] parameterAnnotations) {
        return getOptionalAnnotation(parameterAnnotations) != null;
    }

    @Override
    public ParameterProvider<T> createParameterProvider(Method handleMethod, Class parameterType, Annotation[] parameterAnnotations) {
        Optional optionalAnnotation = getOptionalAnnotation(parameterAnnotations);
        if (optionalAnnotation == null) {
            throw new WebsterException("Could not find optional annotation");
        }
        ParameterValueConverter parameterValueConverter = createParameterValueConverter(optionalAnnotation.parameterValueConverter());
        String defaultValue = getDefaultValueFromAnnotation(optionalAnnotation);
        return new OptionalParameterProvider<>(parameterType, optionalAnnotation, parameterValueConverter, defaultValue);
    }

    private String getDefaultValueFromAnnotation(Optional optionalAnnotation) {
        if (optionalAnnotation.defaultValue().equals(Optional.NOT_SET)) {
            return null;
        }
        return optionalAnnotation.defaultValue();
    }

    private static Optional getOptionalAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Optional) {
                return (Optional) annotation;
            }
        }
        return null;
    }
}
