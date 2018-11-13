package org.visallo.webster;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DefaultRequestBodyValueConverter implements RequestBodyValueConverter {
    private static final Map<Class, Converter> valueConverters = new HashMap<>();

    static {
        registerValueConverter(InputStream.class, new InputStreamConverter());
        registerValueConverter(String.class, new StringConverter());
    }

    public static <T> void registerValueConverter(Class<T> clazz, Converter<T> converter) {
        valueConverters.put(clazz, converter);
    }

    @Override
    public Object toValue(Class parameterType, InputStream in) {
        try {
            if (in == null) {
                return null;
            }
            Converter valueConverter = getValueConverterForType(parameterType);
            if (valueConverter != null) {
                return valueConverter.convert(parameterType, in);
            }
        } catch (Exception ex) {
            throw new WebsterException("Could not process request body: " + parameterType.getName(), ex);
        }
        throw new WebsterException("Inconvertible parameter type for body: " + parameterType.getName());
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

    public interface Converter<T> {
        T convert(Class parameterType, InputStream in) throws Exception;
    }

    public static class InputStreamConverter implements Converter<InputStream> {
        @Override
        public InputStream convert(Class parameterType, InputStream in) {
            return in;
        }
    }

    public static class StringConverter implements Converter<String> {
        @Override
        public String convert(Class parameterType, InputStream in) throws IOException {
            StringBuilder result = new StringBuilder();
            int read;
            byte[] buffer = new byte[1000];
            while ((read = in.read(buffer)) > 0) {
                result.append(new String(buffer, 0, read));
            }
            return result.toString();
        }
    }
}
