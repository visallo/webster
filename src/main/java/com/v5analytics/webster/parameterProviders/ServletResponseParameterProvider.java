package com.v5analytics.webster.parameterProviders;

import com.v5analytics.webster.HandlerChain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletResponseParameterProvider extends ParameterProvider {
    @Override
    public Object getParameter(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) {
        return response;
    }
}
