package com.v5analytics.webster.parameterProviders;

import com.v5analytics.webster.App;
import com.v5analytics.webster.HandlerChain;
import com.v5analytics.webster.Router;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RouterParameterProvider extends ParameterProvider<Router> {
    @Override
    public Router getParameter(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) {
        return App.getApp(request).getRouter();
    }
}
