package org.visallo.webster.handlers;

import org.visallo.webster.HandlerChain;
import org.visallo.webster.RequestResponseHandler;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

public class StaticFileHandler implements RequestResponseHandler {
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
