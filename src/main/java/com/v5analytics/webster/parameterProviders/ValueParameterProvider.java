package com.v5analytics.webster.parameterProviders;

import com.v5analytics.webster.ParameterValueConverter;

import javax.servlet.http.HttpServletRequest;

public abstract class ValueParameterProvider extends ParameterProvider {
    private final Class<?> parameterType;
    private final String parameterName;
    private final ParameterValueConverter parameterValueConverter;

    protected ValueParameterProvider(Class<?> parameterType, String parameterName, ParameterValueConverter parameterValueConverter) {
        this.parameterType = parameterType;
        this.parameterName = parameterName;
        this.parameterValueConverter = parameterValueConverter;
    }

    public String getParameterName() {
        return parameterName;
    }

    protected String getParameterOrAttribute(final HttpServletRequest request) {
        String paramValue = request.getParameter(parameterName);
        if (paramValue == null) {
            Object paramValueObject = request.getAttribute(parameterName);
            if (paramValueObject != null) {
                paramValue = paramValueObject.toString();
            }
            if (paramValue == null) {
                return null;
            }
        }
        return paramValue;
    }

    protected Object toParameterType(String value) {
        return parameterValueConverter.toValue(parameterType, parameterName, value);
    }

    public Class<?> getParameterType() {
        return parameterType;
    }

    public ParameterValueConverter getParameterValueConverter() {
        return parameterValueConverter;
    }
}
