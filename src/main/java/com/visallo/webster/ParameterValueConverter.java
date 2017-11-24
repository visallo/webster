package com.visallo.webster;

public interface ParameterValueConverter {
    Object toValue(Class parameterType, String parameterName, String[] value);
}
