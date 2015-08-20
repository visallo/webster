package com.v5analytics.webster;

public interface ParameterValueConverter {
    Object toValue(Class parameterType, String parameterName, String value);
}
