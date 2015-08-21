package com.v5analytics.webster.parameterProviders;

import com.v5analytics.webster.App;
import com.v5analytics.webster.HandlerChain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppParameterProvider extends ParameterProvider<App> {
    @Override
    public App getParameter(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) {
        return App.getApp(request);
    }
}
