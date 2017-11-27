package org.visallo.webster;

public interface ParameterValueConverter {
    Object toValue(Class parameterType, String parameterName, String[] value);
}
