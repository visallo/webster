package com.visallo.webster.parameterProviders;

import com.visallo.webster.HandlerChain;
import com.visallo.webster.ParameterValueConverter;
import com.visallo.webster.WebsterException;
import com.visallo.webster.annotations.Optional;
import com.visallo.webster.utils.StringUtils;

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
