package com.v5analytics.webster.parameterProviders;

import com.v5analytics.webster.ParameterValueConverter;

import javax.servlet.http.HttpServletRequest;

public abstract class ValueParameterProvider<T> extends ParameterProvider<T> {
    private final Class<?> parameterType;
    private final String parameterName;
    private final ParameterValueConverter parameterValueConverter;

    protected ValueParameterProvider(Class<?> parameterType, String parameterName, ParameterValueConverter parameterValueConverter) {
        this.parameterType = parameterType;
        this.parameterName = parameterName;
        this.parameterValueConverter = parameterValueConverter;
    }

    protected String[] getParameterOrAttribute(final HttpServletRequest request) {
        String[] paramValue = request.getParameterValues(parameterName);
        if (paramValue == null) {
            Object paramValueObject = request.getAttribute(parameterName);
            if (paramValueObject != null) {
                if (paramValueObject.getClass().isArray()) {
                    Object[] arr = (Object[]) paramValueObject;
                    paramValue = new String[arr.length];
                    for (int i = 0; i < arr.length; i++) {
                        paramValue[i] = arr[i] == null ? null : arr[i].toString();
                    }
                } else {
                    paramValue = new String[]{paramValueObject.toString()};
                }
            }
            if (paramValue == null) {
                String headerValue = request.getHeader(parameterName);
                if (headerValue != null) {
                    paramValue = new String[]{headerValue};
                }
            }
        }
        return paramValue;
    }

    public String getParameterName() {
        return parameterName;
    }

    protected T toParameterType(String[] value) {
        return (T) parameterValueConverter.toValue(parameterType, parameterName, value);
    }

    public Class<?> getParameterType() {
        return parameterType;
    }

    public ParameterValueConverter getParameterValueConverter() {
        return parameterValueConverter;
    }
}
