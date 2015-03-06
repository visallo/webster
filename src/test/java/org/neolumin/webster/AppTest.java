package org.neolumin.webster;

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
    private Handler handler;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher requestDispatcher;
    private ServletContext servletContext;
    private App app;

    @Before
    public void before() {
        handler = mock(Handler.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        requestDispatcher = mock(RequestDispatcher.class);
        servletContext = mock(ServletContext.class);
        app = new App(servletContext);
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
        handler = new Handler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) throws Exception {
                throw new ArrayStoreException("boom");
            }
        };
        app.post(path, handler);
        app.onException(ArrayStoreException.class, new TestHandler());
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        app.handle(request, response);
    }

    @Test(expected = Exception.class)
    public void testUnhandledExceptionInHandler() throws Exception {
        handler = new Handler() {
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
        app.delete(path, TestHandler.class);
        when(request.getMethod()).thenReturn("DELETE");
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        app.handle(request, response);
        verify(request).setAttribute("handled", "true");
    }

    @Test
    public void testMultipleHandlersForRoute() throws Exception {
        Handler h2 = new TestHandler();
        app.get(path, h2, handler);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        app.handle(request, response);
        verify(handler).handle(eq(request), eq(response), any(HandlerChain.class));
    }

    @Test
    public void testMulitpleHandlersForRouteSetupByClass() throws Exception {
        app.delete(path, TestHandler.class, TestHandler.class);
        when(request.getMethod()).thenReturn("DELETE");
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        app.handle(request, response);
        verify(request, times(2)).setAttribute("handled", "true");
    }
}
