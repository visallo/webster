package org.visallo.webster.parameterProviders;

import org.visallo.webster.RequestBodyValueConverter;
import org.visallo.webster.WebsterException;
import org.visallo.webster.annotations.RequestBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RequestBodyParameterProviderFactory<T> extends ParameterProviderFactory<T> {
    private static final Map<Class, RequestBodyValueConverter> parameterBodyValueConverters = new HashMap<>();

    protected static RequestBodyValueConverter createParameterBodyValueConverter(Class<? extends RequestBodyValueConverter> parameterBodyValueConverterClass) {
        RequestBodyValueConverter parameterBodyValueConverter = parameterBodyValueConverters.get(parameterBodyValueConverterClass);
        if (parameterBodyValueConverter == null) {
            try {
                parameterBodyValueConverter = parameterBodyValueConverterClass.newInstance();
            } catch (Exception e) {
                throw new WebsterException("Cannot create body value converter: " + parameterBodyValueConverterClass.getName(), e);
            }
            parameterBodyValueConverters.put(parameterBodyValueConverterClass, parameterBodyValueConverter);
        }
        return parameterBodyValueConverter;
    }

    @Override
    public boolean isHandled(Method handleMethod, Class<? extends T> parameterType, Annotation[] parameterAnnotations) {
        return getRequestBodyAnnotation(parameterAnnotations) != null;
    }

    @Override
    public ParameterProvider<T> createParameterProvider(Method handleMethod, Class<?> parameterType, Annotation[] parameterAnnotations) {
        RequestBody requestBodyAnnotation = getRequestBodyAnnotation(parameterAnnotations);
        if (requestBodyAnnotation == null) {
            throw new WebsterException("Could not find " + RequestBody.class.getName() + " annotation");
        }
        RequestBodyValueConverter parameterValueConverter = createParameterBodyValueConverter(requestBodyAnnotation.requestBodyValueConverter());
        return new RequestBodyValueParameterProvider<T>(parameterType, parameterValueConverter);
    }

    private static RequestBody getRequestBodyAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof RequestBody) {
                return (RequestBody) annotation;
            }
        }
        return null;
    }
}
