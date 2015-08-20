package com.v5analytics.webster;

import com.v5analytics.webster.parameterProviders.ParameterProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestUserParameterProvider extends ParameterProvider<TestUser> {
    @Override
    public TestUser getParameter(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) {
        return new TestUser(request.getParameter("userId"));
    }
}
