package org.visallo.webster;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.visallo.webster.annotations.Handle;
import org.visallo.webster.annotations.Optional;
import org.visallo.webster.annotations.Required;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class AppTest {
    private String path = "/foo";
    private RequestResponseHandler handler;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletContext servletContext;
    private App app;
    private RequestResponseHandler missingRouteHandler;

    @Before
    public void before() {
        App.registeredParameterProviderFactory(new TestUserParameterProviderFactory());
        App.registerParameterValueConverter(
                TestParameterObject.class,
                new DefaultParameterValueConverter.SingleValueConverter<TestParameterObject>() {
                    @Override
                    public TestParameterObject convert(
                            Class parameterType,
                            String parameterName,
                            String value
                    ) {
                        return TestParameterObjectExtended.parse(value);
                    }
                }
        );
        handler = mock(RequestResponseHandler.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        servletContext = mock(ServletContext.class);
        app = new App(servletContext);
        missingRouteHandler = mock(RequestResponseHandler.class);
        app.getRouter().setMissingRouteHandler(missingRouteHandler);
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
        ServletOutputStream out = mock(ServletOutputStream.class);

        app.get(path, TestParameterizedHandler.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        when(request.getParameterValues("requiredBoolean")).thenReturn(new String[]{""});
        when(request.getParameterValues("requiredString")).thenReturn(new String[]{"requiredValue"});
        when(request.getParameterValues("requiredInt")).thenReturn(new String[]{"1"});
        when(request.getParameterValues("testParameterObject")).thenReturn(new String[]{"testParameterObjectValue"});
        when(request.getParameterValues("requiredStringArray[]")).thenReturn(new String[]{"requiredStringArrayValue1"});
        when(request.getHeader("requiredStringInHeader")).thenReturn("requiredStringInHeader");
        when(request.getParameterValues("requiredIntegerArray[]")).thenReturn(new String[]{"0", "42", ""});
        when(request.getParameterValues("requiredBooleanArray[]")).thenReturn(new String[]{"true", "false", "", null});
        when(response.getOutputStream()).thenReturn(out);
        app.handle(request, response);
        verify(request).setAttribute("handled", "true");
        verify(out).write("OK".getBytes());
    }

    @Test
    public void testParameterizedHandlerInstance() throws Exception {
        ServletOutputStream out = mock(ServletOutputStream.class);

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
        when(request.getHeader("requiredStringInHeader")).thenReturn("requiredStringInHeader");
        when(request.getParameterValues("requiredIntegerArray[]")).thenReturn(new String[]{"0", "42", ""});
        when(request.getParameterValues("requiredBooleanArray[]")).thenReturn(new String[]{"true", "false", "", null});
        when(response.getOutputStream()).thenReturn(out);
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
        verify(request).setAttribute(
                "testParameterObject",
                new TestParameterObjectExtended("testParameterObjectValue")
        );
        verify(request).setAttribute("requiredStringArray", new String[]{"requiredStringArrayValue1"});
        verify(request).setAttribute("optionalStringArray", null);
        verify(request).setAttribute("requiredStringInHeader", "requiredStringInHeader");
        verify(request).setAttribute("requiredBooleanArray", new Boolean[]{true, false, true, null});
        verify(request).setAttribute("requiredIntegerArray", new Integer[]{0, 42, null});
        verify(request).setAttribute("user", new TestUser("userA"));
        verify(out).write("OK".getBytes());
    }

    @Test(expected = WebsterException.class)
    public void testRequiredParameterNotSet() throws Exception {
        ServletOutputStream out = mock(ServletOutputStream.class);

        app.get(path, TestParameterizedHandler.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        when(request.getParameterValues("requiredBoolean")).thenReturn(new String[]{""});
        when(request.getParameterValues("requiredInt")).thenReturn(new String[]{"1"});
        when(request.getParameterValues("testParameterObject")).thenReturn(new String[]{"testParameterObjectValue"});
        when(request.getParameterValues("requiredStringArray[]")).thenReturn(new String[]{"requiredStringArrayValue1"});
        when(request.getHeader("requiredStringInHeader")).thenReturn("requiredStringInHeader");
        when(response.getOutputStream()).thenReturn(out);
        app.handle(request, response);
        verify(request).setAttribute("handled", "true");
        verify(out).write("OK".getBytes());
    }

    @Test(expected = WebsterException.class)
    public void testRequiredStringEmpty() throws Exception {
        Handler handler = new ParameterizedHandler() {
            @Handle
            public String handle(@Required(name = "param", allowEmpty = false) String param) {
                return "should fail";
            }
        };
        app.get(path, handler);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        when(request.getParameterValues("param")).thenReturn(new String[]{"", "  "});
        app.handle(request, response);
    }

    @Test(expected = WebsterException.class)
    public void testOptionalStringEmpty() throws Exception {
        Handler handler = new ParameterizedHandler() {
            @Handle
            public String handle(@Optional(name = "param", allowEmpty = false) String param) {
                return "should fail";
            }
        };
        app.get(path, handler);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        when(request.getParameterValues("param")).thenReturn(new String[]{"", "  "});
        app.handle(request, response);
    }

    @Test
    public void testRouteMiss() throws Exception {
        app.get(path, handler);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        app.handle(request, response);
        verify(missingRouteHandler).handle(eq(request), eq(response), any(HandlerChain.class));
    }

    @Test
    public void testCaughtExceptionInHandler() throws Exception {
        handler = new RequestResponseHandler() {
            @Override
            public void handle(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    HandlerChain chain
            ) throws Exception {
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
            public void handle(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    HandlerChain chain
            ) throws Exception {
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

    @Test
    public void testGET() throws Exception {
        app.get(path, TestRequestResponseHandler.class);
        testMethod("GET");
    }

    @Test
    public void testPOST() throws Exception {
        app.post(path, TestRequestResponseHandler.class);
        testMethod("POST");
    }

    @Test
    public void testPUT() throws Exception {
        app.put(path, TestRequestResponseHandler.class);
        testMethod("PUT");
    }

    @Test
    public void testDELETE() throws Exception {
        app.delete(path, TestRequestResponseHandler.class);
        testMethod("DELETE");
    }

    @Test
    public void testHEAD() throws Exception {
        app.head(path, TestRequestResponseHandler.class);
        testMethod("HEAD");
    }

    @Test
    public void testOPTIONS() throws Exception {
        app.options(path, TestRequestResponseHandler.class);
        testMethod("OPTIONS");
    }

    @Test
    public void testTRACE() throws Exception {
        app.trace(path, TestRequestResponseHandler.class);
        testMethod("TRACE");
    }

    @Test
    public void testCONNECT() throws Exception {
        app.connect(path, TestRequestResponseHandler.class);
        testMethod("CONNECT");
    }

    private void testMethod(String method) throws Exception {
        when(request.getMethod()).thenReturn(method);
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        app.handle(request, response);
        verify(request).setAttribute("handled", "true");
    }
}
