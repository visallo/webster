package com.v5analytics.webster.parameterProviders;

import com.v5analytics.webster.HandlerChain;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class ServletRequestParameterProviderFactory extends ParameterProviderFactory<ServletRequest> {
    public static final ParameterProvider<ServletRequest> PARAMETER_PROVIDER = new ParameterProvider<ServletRequest>() {
        @Override
        public ServletRequest getParameter(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) {
            return request;
        }
    };

    @Override
    public boolean isHandled(Method handleMethod, Class<? extends ServletRequest> parameterType, java.lang.annotation.Annotation[] parameterAnnotations) {
        return ServletRequest.class.isAssignableFrom(parameterType);
    }

    @Override
    public ParameterProvider<ServletRequest> createParameterProvider(Method handleMethod, Class<?> parameterType, java.lang.annotation.Annotation[] parameterAnnotations) {
        return PARAMETER_PROVIDER;
    }
}
