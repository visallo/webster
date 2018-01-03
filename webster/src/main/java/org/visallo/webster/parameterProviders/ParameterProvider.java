package org.visallo.webster.parameterProviders;

import org.visallo.webster.HandlerChain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class ParameterProvider<T> {
    public abstract T getParameter(HttpServletRequest request, HttpServletResponse response, HandlerChain chain);
}
