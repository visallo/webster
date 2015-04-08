package com.v5analytics.webster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static final String WEBSTER_APP_ATTRIBUTE_NAME = "websterApp";
    private Router router;
    private Map<String, Object> config;

    public App(final ServletContext servletContext) {
        router = new Router(servletContext);
        config = new HashMap<String, Object>();
    }

    public void get(String path, Handler... handlers) {
        router.addRoute(Route.Method.GET, path, handlers);
    }

    public void get(String path, Class<? extends Handler>... classes) {
        try {
            Handler[] handlers = instantiateHandlers(classes);
            get(path, handlers);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void post(String path, Handler... handlers) {
        router.addRoute(Route.Method.POST, path, handlers);
    }

    public void post(String path, Class<? extends Handler>... classes) {
        try {
            Handler[] handlers = instantiateHandlers(classes);
            post(path, handlers);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void put(String path, Handler... handlers) {
        router.addRoute(Route.Method.PUT, path, handlers);
    }

    public void put(String path, Class<? extends Handler>... classes) {
        try {
            Handler[] handlers = instantiateHandlers(classes);
            put(path, handlers);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String path, Handler... handlers) {
        router.addRoute(Route.Method.DELETE, path, handlers);
    }

    public void delete(String path, Class<? extends Handler>... classes) {
        try {
            Handler[] handlers = instantiateHandlers(classes);
            delete(path, handlers);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void onException(Class<? extends Exception> exceptionClass, Handler... handlers) {
        router.addExceptionHandler(exceptionClass, handlers);
    }

    public void onException(Class<? extends Exception> exceptionClass, Class<? extends Handler>... classes) {
        try {
            Handler[] handlers = instantiateHandlers(classes);
            onException(exceptionClass, handlers);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object get(String name) {
        return config.get(name);
    }

    public void set(String name, Object value) {
        config.put(name, value);
    }

    public void enable(String name) {
        config.put(name, true);
    }

    public void disable(String name) {
        config.put(name, false);
    }

    public boolean isEnabled(String name) {
        Object value = config.get(name);
        if (value != null && value instanceof Boolean) {
            return (Boolean) value;
        }
        return false;
    }

    public boolean isDisabled(String name) {
        return !isEnabled(name);
    }

    public Router getRouter() {
        return router;
    }

    public static App getApp(HttpServletRequest request) {
        return (App) request.getAttribute(WEBSTER_APP_ATTRIBUTE_NAME);
    }

    public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long startTime = System.nanoTime();
        try {
            request.setAttribute(WEBSTER_APP_ATTRIBUTE_NAME, this);
            router.route(request, response);
        } finally {
            if (LOGGER.isDebugEnabled()) {
                long endTime = System.nanoTime();
                long timems = (endTime - startTime) / 1000 / 1000;
                LOGGER.debug(request.getMethod() + " " + request.getRequestURI() + " " + timems + "ms");
            }
        }
    }

    protected Handler[] instantiateHandlers(Class<? extends Handler>[] handlerClasses) throws Exception {
        Handler[] handlers = new Handler[handlerClasses.length];
        for (int i = 0; i < handlerClasses.length; i++) {
            handlers[i] = handlerClasses[i].newInstance();
        }
        return handlers;
    }
}
