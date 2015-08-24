package com.v5analytics.webster;

import java.util.HashMap;
import java.util.Map;

public class DefaultParameterValueConverter implements ParameterValueConverter {
    private static final Map<Class, Converter> valueConverters = new HashMap<>();

    static {
        registerValueConverter(Boolean.class, new BooleanConverter());
        registerValueConverter(Boolean.TYPE, new BooleanConverter());
        registerValueConverter(Integer.class, new IntegerConverter());
        registerValueConverter(Integer.TYPE, new IntegerConverter());
        registerValueConverter(Long.class, new LongConverter());
        registerValueConverter(Long.TYPE, new LongConverter());
        registerValueConverter(Double.class, new DoubleConverter());
        registerValueConverter(Double.TYPE, new DoubleConverter());
        registerValueConverter(Float.class, new FloatConverter());
        registerValueConverter(Float.TYPE, new FloatConverter());
        registerValueConverter(String.class, new StringConverter());
        registerValueConverter(String[].class, new StringArrayConverter());
    }

    public static <T> void registerValueConverter(Class<T> clazz, Converter<T> converter) {
        valueConverters.put(clazz, converter);
    }

    @Override
    public Object toValue(Class parameterType, String parameterName, String[] value) {
        try {
            if (value == null) {
                return null;
            }
            Converter valueConverter = valueConverters.get(parameterType);
            if (valueConverter != null) {
                return valueConverter.convert(parameterType, parameterName, value);
            }
        } catch (Exception ex) {
            throw new WebsterException("Could not parse value \"" + toString(value) + "\" for parameter \"" + parameterName + "\"");
        }
        throw new WebsterException("Inconvertible parameter type for parameter \"" + parameterName + "\"");
    }

    private String toString(String[] value) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (String v : value) {
            if (!first) {
                result.append(",");
            }
            result.append(v);
            first = false;
        }
        return result.toString();
    }

    public interface Converter<T> {
        T convert(Class parameterType, String parameterName, String[] value);
    }

    public abstract static class SingleValueConverter<T> implements Converter<T> {
        @Override
        public T convert(Class parameterType, String parameterName, String[] value) {
            if (value.length == 0) {
                return null;
            }
            if (value.length > 1) {
                throw new WebsterException("Too many " + parameterName + " found. Expected 1 found " + value.length);
            }
            return convert(parameterType, parameterName, value[0]);
        }

        public abstract T convert(Class parameterType, String parameterName, String value);
    }

    public static class BooleanConverter extends SingleValueConverter<Boolean> {
        @Override
        public Boolean convert(Class parameterType, String parameterName, String value) {
            if (value == null) {
                return null;
            }
            return value.length() == 0 || Boolean.parseBoolean(value);
        }
    }

    public static class IntegerConverter extends SingleValueConverter<Integer> {
        @Override
        public Integer convert(Class parameterType, String parameterName, String value) {
            if (value == null || value.trim().length() == 0) {
                return null;
            }
            return Integer.parseInt(value);
        }
    }

    public static class LongConverter extends SingleValueConverter<Long> {
        @Override
        public Long convert(Class parameterType, String parameterName, String value) {
            if (value == null || value.trim().length() == 0) {
                return null;
            }
            return Long.parseLong(value);
        }
    }

    public static class DoubleConverter extends SingleValueConverter<Double> {
        @Override
        public Double convert(Class parameterType, String parameterName, String value) {
            if (value == null || value.trim().length() == 0) {
                return null;
            }
            return Double.parseDouble(value);
        }
    }

    public static class FloatConverter extends SingleValueConverter<Float> {
        @Override
        public Float convert(Class parameterType, String parameterName, String value) {
            if (value == null || value.trim().length() == 0) {
                return null;
            }
            return Float.parseFloat(value);
        }
    }

    public static class StringConverter extends SingleValueConverter<String> {
        @Override
        public String convert(Class parameterType, String parameterName, String value) {
            return value;
        }
    }

    public static class StringArrayConverter implements Converter<String[]> {
        @Override
        public String[] convert(Class parameterType, String parameterName, String[] value) {
            return value;
        }
    }
}
