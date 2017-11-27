package org.visallo.webster.parameterProviders;

import org.visallo.webster.ParameterValueConverter;
import org.visallo.webster.WebsterException;

import java.util.HashMap;
import java.util.Map;

public abstract class ValueParameterProviderFactory<T> extends ParameterProviderFactory<T> {
    private static final Map<Class, ParameterValueConverter> parameterValueConverters = new HashMap<>();

    protected static ParameterValueConverter createParameterValueConverter(Class<? extends ParameterValueConverter> parameterValueConverterClass) {
        ParameterValueConverter parameterValueConverter = parameterValueConverters.get(parameterValueConverterClass);
        if (parameterValueConverter == null) {
            try {
                parameterValueConverter = parameterValueConverterClass.newInstance();
            } catch (Exception e) {
                throw new WebsterException("Cannot create value converter: " + parameterValueConverterClass.getName(), e);
            }
            parameterValueConverters.put(parameterValueConverterClass, parameterValueConverter);
        }
        return parameterValueConverter;
    }
}
