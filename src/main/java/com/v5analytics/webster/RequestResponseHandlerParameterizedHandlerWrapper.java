package com.v5analytics.webster;

import com.v5analytics.webster.annotations.Handle;
import com.v5analytics.webster.annotations.Optional;
import com.v5analytics.webster.annotations.Required;
import com.v5analytics.webster.parameterProviders.*;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RequestResponseHandlerParameterizedHandlerWrapper implements RequestResponseHandler {
    private static final Map<Class, ParameterValueConverter> parameterValueConverters = new HashMap<>();
    private static final Map<Class, Class<? extends ParameterProvider>> registeredParameterProviderClasses = new HashMap<>();
    private final ParameterizedHandler handler;
    private final Method handleMethod;
    private final ParameterProvider[] parameterProviders;

    public RequestResponseHandlerParameterizedHandlerWrapper(ParameterizedHandler handler) {
        this.handler = handler;
        this.handleMethod = findMethodWithHandleAnnotation(handler);
        if (this.handleMethod == null) {
            throw new WebsterException("Could not find method annotated with " + Handle.class.getName() + " annotation on class " + handler.getClass().getName());
        }
        parameterProviders = createParameterProviders(this.handleMethod);
    }

    public static void registeredParameterProviderClass(Class handledClass, Class<? extends ParameterProvider> parameterProviderClass) {
        registeredParameterProviderClasses.put(handledClass, parameterProviderClass);
    }

    private static ParameterProvider[] createParameterProviders(Method handleMethod) {
        Class<?>[] parameterTypes = handleMethod.getParameterTypes();
        Annotation[][] allParameterAnnotations = handleMethod.getParameterAnnotations();
        ParameterProvider[] results = new ParameterProvider[parameterTypes.length];
        for (int i = 0; i < results.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            Annotation[] parameterAnnotations = allParameterAnnotations[i];
            ParameterProvider parameterProvider = createParameterProvider(handleMethod, parameterType, parameterAnnotations);
            results[i] = parameterProvider;
        }
        return results;
    }

    private static ParameterProvider createParameterProvider(Method handleMethod, Class<?> parameterType, Annotation[] parameterAnnotations) {
        ParameterProvider parameterProvider;
        Optional optionalAnnotation = getOptionalAnnotation(parameterAnnotations);
        Required requiredAnnotation = getRequiredAnnotation(parameterAnnotations);
        if (ServletRequest.class.isAssignableFrom(parameterType)) {
            parameterProvider = new ServletRequestParameterProvider();
        } else if (ServletResponse.class.isAssignableFrom(parameterType)) {
            parameterProvider = new ServletResponseParameterProvider();
        } else if (HandlerChain.class.isAssignableFrom(parameterType)) {
            parameterProvider = new HandlerChainParameterProvider();
        } else if (App.class.isAssignableFrom(parameterType)) {
            parameterProvider = new AppParameterProvider();
        } else if (Router.class.isAssignableFrom(parameterType)) {
            parameterProvider = new RouterParameterProvider();
        } else if (optionalAnnotation != null) {
            parameterProvider = new OptionalParameterProvider(parameterType, optionalAnnotation, createParameterValueConverter(optionalAnnotation.parameterValueConverter()));
        } else if (requiredAnnotation != null) {
            parameterProvider = new RequiredParameterProvider(parameterType, requiredAnnotation, createParameterValueConverter(requiredAnnotation.parameterValueConverter()));
        } else if (registeredParameterProviderClasses.containsKey(parameterType)) {
            Class<? extends ParameterProvider> parameterProviderClass = registeredParameterProviderClasses.get(parameterType);
            try {
                parameterProvider = parameterProviderClass.newInstance();
            } catch (Exception e) {
                throw new WebsterException("Could not create parameter provider from class " + parameterProviderClass.getName(), e);
            }
        } else {
            throw new WebsterException("Unhandled parameter " + parameterType.getName() + " for method " + handleMethod.getName());
        }
        return parameterProvider;
    }

    private static ParameterValueConverter createParameterValueConverter(Class<? extends ParameterValueConverter> parameterValueConverterClass) {
        ParameterValueConverter parameterValueConverter = parameterValueConverters.get(parameterValueConverterClass);
        if (parameterValueConverter == null) {
            try {
                parameterValueConverter = parameterValueConverterClass.newInstance();
            } catch (Exception e) {
                throw new WebsterException("Cannot create value converter: " + parameterValueConverterClass.getName(), e);
            }
            parameterValueConverters.put(parameterValueConverterClass, parameterValueConverter);
        }
        return parameterValueConverter;
    }

    private static Optional getOptionalAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Optional) {
                return (Optional) annotation;
            }
        }
        return null;
    }

    private static Required getRequiredAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Required) {
                return (Required) annotation;
            }
        }
        return null;
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
        for (int i = 0; i < this.parameterProviders.length; i++) {
            args[i] = this.parameterProviders[i].getParameter(request, response, chain);
        }
        this.handleMethod.invoke(this.handler, args);
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
