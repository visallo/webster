package com.visallo.webster.parameterProviders;

import com.visallo.webster.HandlerChain;
import com.visallo.webster.ParameterValueConverter;
import com.visallo.webster.WebsterException;
import com.visallo.webster.annotations.Required;
import com.visallo.webster.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequiredParameterProvider<T> extends ValueParameterProvider<T> {
    private final Required annotation;

    protected RequiredParameterProvider(Class<?> parameterType, Required annotation, ParameterValueConverter parameterValueConverter) {
        super(parameterType, annotation.name(), parameterValueConverter);
        this.annotation = annotation;
    }

    @Override
    public T getParameter(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) {
        String[] value = getParameterOrAttribute(request);
        if (value == null) {
            throw new WebsterException(String.format("Parameter: '%s' is required in the request", getParameterName()));
        }
        if (!annotation.allowEmpty() && StringUtils.containsAnEmpty(value)) {
            throw new WebsterException(String.format("Parameter: '%s' may not be blank or contain blanks in the request", getParameterName()));
        }
        T result = toParameterType(value);
        if (!isValueValid(result)) {
            throw new WebsterException(String.format("Parameter: '%s' is required in the request", getParameterName()));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private boolean isValueValid(T result) {
        if (result == null) {
            return false;
        }
        if (result.getClass().isArray()) {
            T[] arr = (T[]) result;
            if (arr.length == 0) {
                return false;
            }
            if (arr.length == 1 && arr[0] == null) {
                return false;
            }
        }

        return true;
    }

}
