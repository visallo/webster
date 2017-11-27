package org.visallo.webster.parameterProviders;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public abstract class ParameterProviderFactory<T> {
    public abstract boolean isHandled(Method handleMethod, Class<? extends T> parameterType, Annotation[] parameterAnnotations);

    public abstract ParameterProvider<T> createParameterProvider(Method handleMethod, Class<?> parameterType, Annotation[] parameterAnnotations);
}
