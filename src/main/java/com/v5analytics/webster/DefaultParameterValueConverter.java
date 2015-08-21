package com.v5analytics.webster;

import java.util.HashMap;
import java.util.Map;

public class DefaultParameterValueConverter implements ParameterValueConverter {
    private static final Map<Class, Converter> valueConverters = new HashMap<>();

    static {
        registerValueConverter(Integer.class, new IntegerConverter());
        registerValueConverter(Integer.TYPE, new IntegerConverter());
        registerValueConverter(Long.class, new LongConverter());
        registerValueConverter(Long.TYPE, new LongConverter());
        registerValueConverter(Double.class, new DoubleConverter());
        registerValueConverter(Double.TYPE, new DoubleConverter());
        registerValueConverter(Float.class, new FloatConverter());
        registerValueConverter(Float.TYPE, new FloatConverter());
        registerValueConverter(String.class, new StringConverter());
    }

    public static void registerValueConverter(Class clazz, Converter converter) {
        valueConverters.put(clazz, converter);
    }

    @Override
    public Object toValue(Class parameterType, String parameterName, String value) {
        try {
            if (value == null) {
                return null;
            }
            Converter valueConverter = valueConverters.get(parameterType);
            if (valueConverter != null) {
                return valueConverter.convert(parameterType, parameterName, value);
            }
        } catch (Exception ex) {
            throw new WebsterException("Could not parse value \"" + value + "\" for parameter \"" + parameterName + "\"");
        }
        throw new WebsterException("Inconvertible parameter type for parameter \"" + parameterName + "\"");
    }

    public interface Converter<T> {
        T convert(Class parameterType, String parameterName, String value);
    }

    public static class IntegerConverter implements Converter {
        @Override
        public Object convert(Class parameterType, String parameterName, String value) {
            if (value == null || value.trim().length() == 0) {
                return null;
            }
            return Integer.parseInt(value);
        }
    }

    public static class LongConverter implements Converter {
        @Override
        public Object convert(Class parameterType, String parameterName, String value) {
            if (value == null || value.trim().length() == 0) {
                return null;
            }
            return Long.parseLong(value);
        }
    }

    public static class DoubleConverter implements Converter {
        @Override
        public Object convert(Class parameterType, String parameterName, String value) {
            if (value == null || value.trim().length() == 0) {
                return null;
            }
            return Double.parseDouble(value);
        }
    }

    public static class FloatConverter implements Converter {
        @Override
        public Object convert(Class parameterType, String parameterName, String value) {
            if (value == null || value.trim().length() == 0) {
                return null;
            }
            return Float.parseFloat(value);
        }
    }

    public static class StringConverter implements Converter {
        @Override
        public Object convert(Class parameterType, String parameterName, String value) {
            return value;
        }
    }
}
