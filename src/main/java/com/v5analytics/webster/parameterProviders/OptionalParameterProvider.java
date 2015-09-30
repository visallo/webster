package com.v5analytics.webster.parameterProviders;

import com.v5analytics.webster.HandlerChain;
import com.v5analytics.webster.ParameterValueConverter;
import com.v5analytics.webster.WebsterException;
import com.v5analytics.webster.annotations.Optional;
import com.v5analytics.webster.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OptionalParameterProvider<T> extends ValueParameterProvider<T> {
    private final String defaultValue;
    private final Optional annotation;

    public OptionalParameterProvider(Class<?> parameterType, Optional annotation, ParameterValueConverter parameterValueConverter, String defaultValue) {
        super(parameterType, annotation.name(), parameterValueConverter);
        this.annotation = annotation;
        this.defaultValue = defaultValue;
    }

    @Override
    public T getParameter(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) {
        String[] value = getParameterOrAttribute(request);
        if (value == null) {
            if (defaultValue == null) {
                value = null;
            } else {
                value = new String[]{defaultValue};
            }
        } else {
            if (!annotation.allowEmpty() && StringUtils.containsAnEmpty(value)) {
                throw new WebsterException(String.format("Parameter: '%s' may not be or contain blanks in the request", getParameterName()));
            }
        }
        return toParameterType(value);
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
