package org.visallo.webster.parameterProviders;

import org.visallo.webster.HandlerChain;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class ServletResponseParameterProviderFactory extends ParameterProviderFactory<ServletResponse> {
    public static final ParameterProvider<ServletResponse> PARAMETER_PROVIDER = new ParameterProvider<ServletResponse>() {
        @Override
        public ServletResponse getParameter(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) {
            return response;
        }
    };

    @Override
    public boolean isHandled(Method handleMethod, Class<? extends ServletResponse> parameterType, Annotation[] parameterAnnotations) {
        return ServletResponse.class.isAssignableFrom(parameterType);
    }

    @Override
    public ParameterProvider<ServletResponse> createParameterProvider(Method handleMethod, Class<?> parameterType, Annotation[] parameterAnnotations) {
        return PARAMETER_PROVIDER;
    }
}
