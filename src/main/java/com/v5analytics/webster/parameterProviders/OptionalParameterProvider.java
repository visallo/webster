package com.v5analytics.webster.parameterProviders;

import com.v5analytics.webster.HandlerChain;
import com.v5analytics.webster.ParameterValueConverter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OptionalParameterProvider<T> extends ValueParameterProvider<T> {
    private final String defaultValue;

    public OptionalParameterProvider(Class<?> parameterType, String parameterName, ParameterValueConverter parameterValueConverter, String defaultValue) {
        super(parameterType, parameterName, parameterValueConverter);
        this.defaultValue = defaultValue;
    }

    @Override
    public T getParameter(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) {
        String value = getParameterOrAttribute(request);
        if (value == null) {
            value = defaultValue;
        }
        return toParameterType(value);
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
