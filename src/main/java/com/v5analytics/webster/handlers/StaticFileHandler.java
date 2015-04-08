package com.v5analytics.webster.handlers;

import com.v5analytics.webster.Handler;
import com.v5analytics.webster.HandlerChain;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

public class StaticFileHandler implements Handler {
    private final RequestDispatcher handler;
    private final String pathInfo;

    public StaticFileHandler(ServletContext servletContext) {
        this(servletContext, null);
    }

    public StaticFileHandler(ServletContext servletContext, String pathInfo) {
        this.handler = servletContext.getNamedDispatcher("default");
        this.pathInfo = pathInfo;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) throws Exception {
        HttpServletRequest wrapped = new HttpServletRequestWrapper(request) {
            @Override
            public String getServletPath() {
                return "";
            }

            @Override
            public String getPathInfo() {
                if (pathInfo == null) {
                    return super.getPathInfo();
                } else {
                    return pathInfo;
                }
            }
        };
        handler.forward(wrapped, response);
    }
}
