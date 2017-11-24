package com.visallo.webster;

import com.visallo.webster.annotations.Handle;
import com.visallo.webster.parameterProviders.*;
import com.visallo.webster.resultWriters.ResultWriter;
import com.visallo.webster.resultWriters.ResultWriterFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RequestResponseHandlerParameterizedHandlerWrapper implements RequestResponseHandler {
    private static final List<ParameterProviderFactory> registeredParameterProviderFactories = new ArrayList<>();
    private final ParameterizedHandler handler;
    private final Method handleMethod;
    private final ParameterProvider[] parameterProviders;
    private final ResultWriter resultWriter;

    static {
        registeredParameterProviderFactories.add(new AppParameterProviderFactory());
        registeredParameterProviderFactories.add(new HandlerChainParameterProviderFactory());
        registeredParameterProviderFactories.add(new OptionalParameterProviderFactory());
        registeredParameterProviderFactories.add(new RequiredParameterProviderFactory());
        registeredParameterProviderFactories.add(new RouterParameterProviderFactory());
        registeredParameterProviderFactories.add(new ServletRequestParameterProviderFactory());
        registeredParameterProviderFactories.add(new ServletResponseParameterProviderFactory());
    }

    public RequestResponseHandlerParameterizedHandlerWrapper(App app, ParameterizedHandler handler) {
        this.handler = handler;
        this.handleMethod = findMethodWithHandleAnnotation(handler);
        if (this.handleMethod == null) {
            throw new WebsterException("Could not find method annotated with " + Handle.class.getName() + " annotation on class " + handler.getClass().getName());
        }
        parameterProviders = createParameterProviders(this.handleMethod);
        resultWriter = createResultWriter(app, this.handleMethod);
    }

    private ResultWriter createResultWriter(App app, Method handleMethod) {
        ResultWriterFactory resultWriterFactory = app.internalGetResultWriterFactory(handleMethod);
        return resultWriterFactory.createResultWriter(handleMethod);
    }

    public static <T> void registeredParameterProviderFactory(ParameterProviderFactory<T> parameterProviderFactory) {
        registeredParameterProviderFactories.add(parameterProviderFactory);
    }

    private static ParameterProvider[] createParameterProviders(Method handleMethod) {
        Class<?>[] parameterTypes = handleMethod.getParameterTypes();
        Annotation[][] allParameterAnnotations = handleMethod.getParameterAnnotations();
        ParameterProvider[] results = new ParameterProvider[parameterTypes.length];
        for (int i = 0; i < results.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            Annotation[] parameterAnnotations = allParameterAnnotations[i];
            results[i] = createParameterProvider(handleMethod, parameterType, parameterAnnotations);
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    private static ParameterProvider createParameterProvider(Method handleMethod, Class<?> parameterType, Annotation[] parameterAnnotations) {
        ParameterProvider parameterProvider = null;
        for (ParameterProviderFactory registeredParameterProviderFactory : registeredParameterProviderFactories) {
            if (registeredParameterProviderFactory.isHandled(handleMethod, parameterType, parameterAnnotations)) {
                parameterProvider = registeredParameterProviderFactory.createParameterProvider(handleMethod, parameterType, parameterAnnotations);
                break;
            }
        }
        if (parameterProvider == null) {
            throw new WebsterException("Unhandled parameter " + parameterType.getName() + " for method " + handleMethod.getName() + " in class " + handleMethod.getDeclaringClass().getName());
        }
        return parameterProvider;
    }

    private static Method findMethodWithHandleAnnotation(ParameterizedHandler handler) {
        for (Method method : handler.getClass().getMethods()) {
            Handle handlerMethodAnnotation = method.getAnnotation(Handle.class);
            if (handlerMethodAnnotation != null) {
                return method;
            }
        }
        return null;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) throws Exception {
        Object[] args = new Object[this.parameterProviders.length];
        try {
            for (int i = 0; i < this.parameterProviders.length; i++) {
                args[i] = this.parameterProviders[i].getParameter(request, response, chain);
            }
        } catch (Exception ex) {
            throw new WebsterException("Could not parse arguments for path " + request.getRequestURI(), ex);
        }
        try {
            Object result = this.handleMethod.invoke(this.handler, args);
            this.resultWriter.write(result, request, response, chain);
        } catch (IllegalArgumentException ex) {
            throw new WebsterException("Could not invoke " + this.handleMethod, ex);
        }
    }

    public ParameterizedHandler getHandler() {
        return handler;
    }

    public Method getHandleMethod() {
        return handleMethod;
    }

    public ParameterProvider[] getParameterProviders() {
        return parameterProviders;
    }
}
