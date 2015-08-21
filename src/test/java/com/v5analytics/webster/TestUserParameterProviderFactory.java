package com.v5analytics.webster;

import com.v5analytics.webster.parameterProviders.ParameterProvider;
import com.v5analytics.webster.parameterProviders.ParameterProviderFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class TestUserParameterProviderFactory extends ParameterProviderFactory<TestUser> {
    @Override
    public boolean isHandled(Method handleMethod, Class<? extends TestUser> parameterType, Annotation[] parameterAnnotations) {
        return TestUser.class.isAssignableFrom(parameterType);
    }

    @Override
    public ParameterProvider<TestUser> createParameterProvider(Method handleMethod, Class<?> parameterType, Annotation[] parameterAnnotations) {
        return new ParameterProvider<TestUser>() {
            @Override
            public TestUser getParameter(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) {
                return new TestUser(request.getParameter("userId"));
            }
        };
    }
}
