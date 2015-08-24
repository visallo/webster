package com.v5analytics.webster;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class AppTest {
    private String path = "/foo";
    private RequestResponseHandler handler;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher requestDispatcher;
    private ServletContext servletContext;
    private App app;

    @Before
    public void before() {
        App.registeredParameterProviderFactory(new TestUserParameterProviderFactory());
        App.registerParameterValueConverter(TestParameterObject.class, new DefaultParameterValueConverter.SingleValueConverter<TestParameterObject>() {
            @Override
            public TestParameterObject convert(Class parameterType, String parameterName, String value) {
                return TestParameterObject.parse(value);
            }
        });
        handler = mock(RequestResponseHandler.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        requestDispatcher = mock(RequestDispatcher.class);
        servletContext = mock(ServletContext.class);
        app = new App(servletContext);
        when(request.getAttribute(App.WEBSTER_APP_ATTRIBUTE_NAME)).thenReturn(app);
    }

    @Test
    public void testRouteMatch() throws Exception {
        app.get(path, handler);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        app.handle(request, response);
        verify(handler).handle(eq(request), eq(response), any(HandlerChain.class));
    }

    @Test
    public void testParameterizedHandlerClass() throws Exception {
        app.get(path, TestParameterizedHandler.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        when(request.getParameterValues("requiredBoolean")).thenReturn(new String[]{""});
        when(request.getParameterValues("requiredString")).thenReturn(new String[]{"requiredValue"});
        when(request.getParameterValues("requiredInt")).thenReturn(new String[]{"1"});
        when(request.getParameterValues("testParameterObject")).thenReturn(new String[]{"testParameterObjectValue"});
        when(request.getParameterValues("requiredStringArray[]")).thenReturn(new String[]{"requiredStringArrayValue1"});
        app.handle(request, response);
        verify(request).setAttribute("handled", "true");
    }

    @Test
    public void testParameterizedHandlerInstance() throws Exception {
        ParameterizedHandler parameterizedHandler = new TestParameterizedHandler();
        app.get(path, parameterizedHandler);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        when(request.getParameterValues("requiredBoolean")).thenReturn(new String[]{""});
        when(request.getParameterValues("requiredString")).thenReturn(new String[]{"requiredValue"});
        when(request.getParameterValues("requiredInt")).thenReturn(new String[]{"1"});
        when(request.getParameter("userId")).thenReturn("userA");
        when(request.getParameterValues("testParameterObject")).thenReturn(new String[]{"testParameterObjectValue"});
        when(request.getParameterValues("requiredStringArray[]")).thenReturn(new String[]{"requiredStringArrayValue1"});
        app.handle(request, response);
        verify(request).setAttribute("handled", "true");
        verify(request).setAttribute("requiredBoolean", true);
        verify(request).setAttribute("optionalBooleanWithDefault", false);
        verify(request).setAttribute("requiredInt", 1);
        verify(request).setAttribute("optionalIntWithDefault", 42);
        verify(request).setAttribute("optionalInteger", null);
        verify(request).setAttribute("optionalIntegerWithDefault", 42);
        verify(request).setAttribute("requiredString", "requiredValue");
        verify(request).setAttribute("optionalStringWithDefault", "default value");
        verify(request).setAttribute("testParameterObject", new TestParameterObject("testParameterObjectValue"));
        verify(request).setAttribute("requiredStringArray", new String[]{"requiredStringArrayValue1"});
        verify(request).setAttribute("optionalStringArray", null);
        verify(request).setAttribute("user", new TestUser("userA"));
    }

    @Test
    public void testRouteMiss() throws Exception {
        app.get(path, handler);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        when(servletContext.getNamedDispatcher(anyString())).thenReturn(requestDispatcher);
        app.handle(request, response);
        verify(requestDispatcher).forward(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    public void testCaughtExceptionInHandler() throws Exception {
        handler = new RequestResponseHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) throws Exception {
                throw new ArrayStoreException("boom");
            }
        };
        app.post(path, handler);
        app.onException(ArrayStoreException.class, new TestRequestResponseHandler());
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        app.handle(request, response);
    }

    @Test(expected = Exception.class)
    public void testUnhandledExceptionInHandler() throws Exception {
        handler = new RequestResponseHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) throws Exception {
                throw new Exception("boom");
            }
        };
        app.post(path, handler);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        app.handle(request, response);
    }

    @Test
    public void testRouteSetupByClass() throws Exception {
        app.delete(path, TestRequestResponseHandler.class);
        when(request.getMethod()).thenReturn("DELETE");
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        app.handle(request, response);
        verify(request).setAttribute("handled", "true");
    }

    @Test
    public void testMultipleHandlersForRoute() throws Exception {
        Handler h2 = new TestRequestResponseHandler();
        app.get(path, h2, handler);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        app.handle(request, response);
        verify(handler).handle(eq(request), eq(response), any(HandlerChain.class));
    }

    @Test
    public void testMulitpleHandlersForRouteSetupByClass() throws Exception {
        app.delete(path, TestRequestResponseHandler.class, TestRequestResponseHandler.class);
        when(request.getMethod()).thenReturn("DELETE");
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        app.handle(request, response);
        verify(request, times(2)).setAttribute("handled", "true");
    }
}
