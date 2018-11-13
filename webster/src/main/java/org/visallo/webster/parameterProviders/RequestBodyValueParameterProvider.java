package org.visallo.webster.parameterProviders;

import org.visallo.webster.HandlerChain;
import org.visallo.webster.RequestBodyValueConverter;
import org.visallo.webster.WebsterException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RequestBodyValueParameterProvider<T> extends ParameterProvider<T> {
    private final Class<?> parameterType;
    private final RequestBodyValueConverter parameterValueConverter;

    protected RequestBodyValueParameterProvider(
            Class<?> parameterType,
            RequestBodyValueConverter parameterValueConverter
    ) {
        this.parameterType = parameterType;
        this.parameterValueConverter = parameterValueConverter;
    }

    @Override
    public T getParameter(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) {
        ServletInputStream in;
        try {
            in = request.getInputStream();
        } catch (IOException ex) {
            throw new WebsterException("Could not get request input stream", ex);
        }
        return (T) getParameterBodyValueConverter().toValue(getParameterType(), in);
    }


    public Class<?> getParameterType() {
        return parameterType;
    }

    public RequestBodyValueConverter getParameterBodyValueConverter() {
        return parameterValueConverter;
    }
}
