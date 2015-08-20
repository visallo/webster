package com.v5analytics.webster.parameterProviders;

import com.v5analytics.webster.HandlerChain;
import com.v5analytics.webster.ParameterValueConverter;
import com.v5analytics.webster.annotations.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OptionalParameterProvider extends ValueParameterProvider {
    private final String defaultValue;

    public OptionalParameterProvider(Class<?> parameterType, Optional optionalAnnotation, ParameterValueConverter parameterValueConverter) {
        super(parameterType, optionalAnnotation.name(), parameterValueConverter);
        defaultValue = optionalAnnotation.defaultValue();
    }

    @Override
    public Object getParameter(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) {
        String value = getParameterOrAttribute(request);
        if (value == null && !defaultValue.equals(Optional.NOT_SET)) {
            value = defaultValue;
        }
        return toParameterType(value);
    }
}
