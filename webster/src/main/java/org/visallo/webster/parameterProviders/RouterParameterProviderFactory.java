package org.visallo.webster.parameterProviders;

import org.visallo.webster.App;
import org.visallo.webster.HandlerChain;
import org.visallo.webster.Router;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class RouterParameterProviderFactory extends ParameterProviderFactory<Router> {
    private static final ParameterProvider<Router> PARAMETER_PROVIDER = new ParameterProvider<Router>() {
        @Override
        public Router getParameter(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) {
            return App.getApp(request).getRouter();
        }
    };

    @Override
    public boolean isHandled(Method handleMethod, Class<? extends Router> parameterType, Annotation[] parameterAnnotations) {
        return Router.class.isAssignableFrom(parameterType);
    }

    @Override
    public ParameterProvider<Router> createParameterProvider(Method handleMethod, Class<?> parameterType, Annotation[] parameterAnnotations) {
        return PARAMETER_PROVIDER;
    }
}
