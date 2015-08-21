package com.v5analytics.webster.parameterProviders;

import com.v5analytics.webster.HandlerChain;
import com.v5analytics.webster.ParameterValueConverter;
import com.v5analytics.webster.WebsterException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequiredParameterProvider<T> extends ValueParameterProvider<T> {
    protected RequiredParameterProvider(Class<?> parameterType, String parameterName, ParameterValueConverter parameterValueConverter) {
        super(parameterType, parameterName, parameterValueConverter);
    }

    @Override
    public T getParameter(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) {
        String value = getParameterOrAttribute(request);
        if (value == null) {
            throw new WebsterException(String.format("Parameter: '%s' is required in the request", getParameterName()));
        }
        T result = toParameterType(value);
        if (result == null) {
            throw new WebsterException(String.format("Parameter: '%s' is required in the request", getParameterName()));
        }
        return result;
    }
}
