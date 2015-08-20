package com.v5analytics.webster.parameterProviders;

import com.v5analytics.webster.HandlerChain;
import com.v5analytics.webster.ParameterValueConverter;
import com.v5analytics.webster.WebsterException;
import com.v5analytics.webster.annotations.Required;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequiredParameterProvider extends ValueParameterProvider {
    public RequiredParameterProvider(Class<?> parameterType, Required requiredAnnotation, ParameterValueConverter parameterValueConverter) {
        super(parameterType, requiredAnnotation.name(), parameterValueConverter);
    }

    @Override
    public Object getParameter(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) {
        String value = getParameterOrAttribute(request);
        if (value == null) {
            throw new WebsterException(String.format("Parameter: '%s' is required in the request", getParameterName()));
        }
        return toParameterType(value);
    }
}
