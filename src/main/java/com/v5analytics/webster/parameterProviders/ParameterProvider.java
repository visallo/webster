package com.v5analytics.webster.parameterProviders;

import com.v5analytics.webster.HandlerChain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class ParameterProvider<T> {
    public abstract T getParameter(HttpServletRequest request, HttpServletResponse response, HandlerChain chain);
}
