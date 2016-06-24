package com.v5analytics.webster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Router {
    private static final Logger LOGGER = LoggerFactory.getLogger(Router.class);

    private ServletContext servletContext;
    private Map<Route.Method, List<Route>> routes = new HashMap<Route.Method, List<Route>>();
    Map<Class<? extends Exception>, RequestResponseHandler[]> exceptionHandlers = new HashMap<>();

    public Router(ServletContext servletContext) {
        this.servletContext = servletContext;
        routes.put(Route.Method.GET, new ArrayList<Route>());
        routes.put(Route.Method.POST, new ArrayList<Route>());
        routes.put(Route.Method.PUT, new ArrayList<Route>());
        routes.put(Route.Method.DELETE, new ArrayList<Route>());
        routes.put(Route.Method.HEAD, new ArrayList<Route>());
        routes.put(Route.Method.OPTIONS, new ArrayList<Route>());
        routes.put(Route.Method.TRACE, new ArrayList<Route>());
        routes.put(Route.Method.CONNECT, new ArrayList<Route>());
    }

    public Route addRoute(Route.Method method, String path, RequestResponseHandler... handlers) {
        List<Route> methodRoutes = routes.get(method);
        Route route = new Route(method, path, handlers);
        int existingRouteIndex = methodRoutes.indexOf(route);

        if (existingRouteIndex > -1) {
            methodRoutes.set(existingRouteIndex, route);
        } else {
            methodRoutes.add(route);
        }

        return route;
    }

    public void addExceptionHandler(Class<? extends Exception> exceptionClass, RequestResponseHandler[] handlers) {
        exceptionHandlers.put(exceptionClass, handlers);
    }

    public void route(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            routeWithExceptionHandling(request, response);
        } catch (Exception ex) {
            RequestResponseHandler[] handlers = exceptionHandlers.get(ex.getClass());
            if (handlers != null && handlers.length > 0) {
                LOGGER.error("Caught exception in route: " + request.getRequestURI(), ex);
                dispatch(handlers, request, response);
            } else {
                throw ex;
            }
        }
    }

    private void routeWithExceptionHandling(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Route.Method method = Route.Method.valueOf(request.getMethod().toUpperCase());

        if (method == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath == null) {
            contextPath = "";
        }
        String relativeUri = requestURI.substring(contextPath.length());
        if (relativeUri.length() == 0) {
            response.sendRedirect(contextPath + '/');
            return;
        }

        Route route = findRoute(method, request, relativeUri);

        if (route == null) {
            RequestDispatcher rd = servletContext.getNamedDispatcher("default");
            if (rd == null) {
                throw new WebsterException("Could not get named dispatcher 'default'");
            }
            HttpServletRequest wrapped = new HttpServletRequestWrapper(request) {
                public String getServletPath() {
                    return "";
                }
            };
            rd.forward(wrapped, response);
        } else {
            RequestResponseHandler[] handlers = route.getHandlers();
            dispatch(handlers, request, response);
        }
    }

    private void dispatch(
            RequestResponseHandler[] handlers,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        HandlerChain chain = new HandlerChain(handlers);
        chain.next(request, response);
    }

    private Route findRoute(Route.Method method, HttpServletRequest request, String relativeUri) {
        List<Route> potentialRoutes = routes.get(method);
        for (Route route : potentialRoutes) {
            if (route.isMatch(request, relativeUri)) {
                return route;
            }
        }
        return null;
    }

    public Map<Route.Method, List<Route>> getRoutes() {
        return routes;
    }
}
