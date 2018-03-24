package org.visallo.webster;

import org.visallo.webster.utils.StdDateFormat;

import java.lang.reflect.Array;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DefaultParameterValueConverter implements ParameterValueConverter {
    private static final Map<Class, Converter> valueConverters = new HashMap<>();

    static {
        registerValueConverter(Boolean.class, new BooleanConverter());
        registerValueConverter(Boolean.TYPE, new BooleanConverter());
        registerValueConverter(Boolean[].class, new BooleanArrayConverter());
        registerValueConverter(Integer.class, new IntegerConverter());
        registerValueConverter(Integer.TYPE, new IntegerConverter());
        registerValueConverter(Integer[].class, new IntegerArrayConverter());
        registerValueConverter(Long.class, new LongConverter());
        registerValueConverter(Long.TYPE, new LongConverter());
        registerValueConverter(Long[].class, new LongArrayConverter());
        registerValueConverter(Double.class, new DoubleConverter());
        registerValueConverter(Double.TYPE, new DoubleConverter());
        registerValueConverter(Double[].class, new DoubleArrayConverter());
        registerValueConverter(Float.class, new FloatConverter());
        registerValueConverter(Float.TYPE, new FloatConverter());
        registerValueConverter(Float[].class, new FloatArrayConverter());
        registerValueConverter(String.class, new StringConverter());
        registerValueConverter(String[].class, new StringArrayConverter());
        registerValueConverter(ZonedDateTime.class, new ZonedDateTimeConverter());
        registerValueConverter(ZonedDateTime[].class, new ZonedDateTimeArrayConverter());
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
            Converter valueConverter = getValueConverterForType(parameterType);
            if (valueConverter != null) {
                return valueConverter.convert(parameterType, parameterName, value);
            }
        } catch (Exception ex) {
            throw new WebsterException("Could not parse value \"" + toString(value) + "\" for parameter \"" + parameterName + "\"", ex);
        }
        throw new WebsterException("Inconvertible parameter type for parameter \"" + parameterName + "\"");
    }

    private Converter getValueConverterForType(Class parameterType) {
        Converter valueConverter = valueConverters.get(parameterType);
        if (valueConverter != null) {
            return valueConverter;
        }
        for (Map.Entry<Class, Converter> classConverterEntry : valueConverters.entrySet()) {
            if (classConverterEntry.getKey().isAssignableFrom(parameterType)) {
                return classConverterEntry.getValue();
            }
        }
        return null;
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

    public abstract static class ArrayValueConverter<T> implements Converter<T[]> {
        private final Class<T> convertedType;

        public ArrayValueConverter(Class<T> convertedType) {
            this.convertedType = convertedType;
        }

        @Override
        public T[] convert(Class parameterType, String parameterName, String[] value) {
            if (value == null || value.length == 0) {
                return null;
            }

            @SuppressWarnings("unchecked")
            T[] result = (T[]) Array.newInstance(convertedType, value.length);

            for (int i = 0; i < value.length; i++) {
                if (value[i] == null || value[i].trim().length() == 0) {
                    result[i] = convertNullOrEmpty(parameterType, parameterName, value[i]);
                } else {
                    result[i] = convert(parameterType, parameterName, value[i]);
                }
            }
            return result;
        }

        public T convertNullOrEmpty(Class parameterType, String parameterName, String rawValue) {
            return null;
        }

        public abstract T convert(Class parameterType, String parameterName, String rawValue);
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

    public static class StringArrayConverter extends ArrayValueConverter<String> {
        public StringArrayConverter() {
            super(String.class);
        }

        @Override
        public String convert(Class parameterType, String parameterName, String value) {
            return value;
        }
    }

    public static class ZonedDateTimeConverter extends SingleValueConverter<ZonedDateTime> {
        public static ZoneId OUTPUT_ZONE = ZoneId.systemDefault();

        @Override
        public ZonedDateTime convert(Class parameterType, String parameterName, String value) {
            return parseDate(value);
        }

        private static ZonedDateTime parseDate(String value) {
            if (value == null || "null".equals(value)) {
                return null;
            }

            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }

            try {
                Date dateInSystemZone = StdDateFormat.instance.parse(value);
                return ZonedDateTime.ofInstant(dateInSystemZone.toInstant(), ZoneId.systemDefault())
                        .withZoneSameInstant(OUTPUT_ZONE);
            } catch (Exception ex) {
                throw new WebsterException("Could not parse date: " + value, ex);
            }
        }
    }

    public static class ZonedDateTimeArrayConverter extends ArrayValueConverter<ZonedDateTime> {
        public ZonedDateTimeArrayConverter() {
            super(ZonedDateTime.class);
        }

        @Override
        public ZonedDateTime convert(Class parameterType, String parameterName, String value) {
            return ZonedDateTimeConverter.parseDate(value);
        }
    }

    public static class BooleanArrayConverter extends ArrayValueConverter<Boolean> {
        public BooleanArrayConverter() {
            super(Boolean.class);
        }

        @Override
        public Boolean convertNullOrEmpty(Class parameterType, String parameterName, String value) {
            if (value == null) {
                return null;
            }
            return true;
        }

        @Override
        public Boolean convert(Class parameterType, String parameterName, String value) {
            return Boolean.parseBoolean(value);
        }
    }

    public static class IntegerArrayConverter extends ArrayValueConverter<Integer> {
        public IntegerArrayConverter() {
            super(Integer.class);
        }

        @Override
        public Integer convert(Class parameterType, String parameterName, String value) {
            return Integer.parseInt(value);
        }
    }

    public static class LongArrayConverter extends ArrayValueConverter<Long> {
        public LongArrayConverter() {
            super(Long.class);
        }

        @Override
        public Long convert(Class parameterType, String parameterName, String value) {
            return Long.parseLong(value);
        }
    }

    public static class FloatArrayConverter extends ArrayValueConverter<Float> {
        public FloatArrayConverter() {
            super(Float.class);
        }

        @Override
        public Float convert(Class parameterType, String parameterName, String value) {
            return Float.parseFloat(value);
        }
    }

    public static class DoubleArrayConverter extends ArrayValueConverter<Double> {
        public DoubleArrayConverter() {
            super(Double.class);
        }

        @Override
        public Double convert(Class parameterType, String parameterName, String value) {
            return Double.parseDouble(value);
        }
    }
}
