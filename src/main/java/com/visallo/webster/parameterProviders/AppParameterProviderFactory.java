package com.visallo.webster.parameterProviders;

import com.visallo.webster.App;
import com.visallo.webster.HandlerChain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AppParameterProviderFactory extends ParameterProviderFactory<App> {
    private static final ParameterProvider<App> PARAMETER_PROVIDER = new ParameterProvider<App>() {
        @Override
        public App getParameter(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) {
            return App.getApp(request);
        }
    };

    @Override
    public boolean isHandled(Method handleMethod, Class<? extends App> parameterType, Annotation[] parameterAnnotations) {
        return App.class.isAssignableFrom(parameterType);
    }

    @Override
    public ParameterProvider<App> createParameterProvider(Method handleMethod, Class<?> parameterType, Annotation[] parameterAnnotations) {
        return PARAMETER_PROVIDER;
    }
}
