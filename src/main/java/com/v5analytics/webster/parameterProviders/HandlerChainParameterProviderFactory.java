package com.v5analytics.webster.parameterProviders;

import com.v5analytics.webster.HandlerChain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class HandlerChainParameterProviderFactory extends ParameterProviderFactory<HandlerChain> {
    public static final ParameterProvider<HandlerChain> PARAMETER_PROVIDER = new ParameterProvider<HandlerChain>() {
        @Override
        public HandlerChain getParameter(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) {
            return chain;
        }
    };

    @Override
    public boolean isHandled(Method handleMethod, Class parameterType, Annotation[] parameterAnnotations) {
        return HandlerChain.class.isAssignableFrom(parameterType);
    }

    @Override
    public ParameterProvider<HandlerChain> createParameterProvider(Method handleMethod, Class parameterType, Annotation[] parameterAnnotations) {
        return PARAMETER_PROVIDER;
    }
}
