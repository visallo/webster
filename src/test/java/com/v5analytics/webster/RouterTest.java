package com.v5analytics.webster;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class RouterTest {
    private Router router;
    private RequestResponseHandler handler;
    private RequestResponseHandler exceptionThrowingHandler;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestResponseHandler missingRouteHandler;
    private String path = "/foo";

    @Before
    public void before() {
        ServletContext servletContext = mock(ServletContext.class);
        router = new Router(servletContext);
        handler = mock(RequestResponseHandler.class);
        exceptionThrowingHandler = mock(RequestResponseHandler.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        missingRouteHandler = mock(RequestResponseHandler.class);
        router.setMissingRouteHandler(missingRouteHandler);
    }

    @Test
    public void testSimpleRoute() throws Exception {
        router.addRoute(Route.Method.GET, path, handler);
        when(request.getMethod()).thenReturn(Route.Method.GET.toString());
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        router.route(request, response);
        verify(handler).handle(eq(request), eq(response), any(HandlerChain.class));
    }

    @Test
    public void testRouteWithComponent() throws Exception {
        router.addRoute(Route.Method.GET, path + "/{id}/text", handler);
        when(request.getMethod()).thenReturn(Route.Method.GET.toString());
        when(request.getRequestURI()).thenReturn(path + "/25/text");
        when(request.getContextPath()).thenReturn("");
        router.route(request, response);
        verify(handler).handle(eq(request), eq(response), any(HandlerChain.class));
        verify(request).setAttribute("id", "25");
    }

    @Test
    public void testMultipleRoutesWithSamePrefix() throws Exception {
        router.addRoute(Route.Method.GET, path + "/{id}/text", handler);
        Route routeRaw = router.addRoute(Route.Method.GET, path + "/{id}/raw", handler);
        router.addRoute(Route.Method.GET, path + "/{id}", handler);
        when(request.getMethod()).thenReturn(Route.Method.GET.toString());
        when(request.getRequestURI()).thenReturn(path + "/25/raw");
        when(request.getContextPath()).thenReturn("");
        router.route(request, response);
        verify(handler).handle(eq(request), eq(response), any(HandlerChain.class));
        verify(request).setAttribute("id", "25");
        verify(request).setAttribute(Route.MATCHED_ROUTE, routeRaw);
    }

    @Test
    public void testMultipleRoutesWithSamePrefixLastMatch() throws Exception {
        router.addRoute(Route.Method.GET, path + "/{id}/text", handler);
        router.addRoute(Route.Method.GET, path + "/{id}/raw", handler);
        Route defaultRoute = router.addRoute(Route.Method.GET, path + "/{id}", handler);
        when(request.getMethod()).thenReturn(Route.Method.GET.toString());
        when(request.getRequestURI()).thenReturn(path + "/25/zzz");
        when(request.getContextPath()).thenReturn("");
        router.route(request, response);
        verify(handler).handle(eq(request), eq(response), any(HandlerChain.class));
        verify(request).setAttribute("id", "25/zzz");
        verify(request).setAttribute(Route.MATCHED_ROUTE, defaultRoute);
    }

    @Test
    public void testRouteMissingDueToMethod() throws Exception {
        router.addRoute(Route.Method.GET, path, handler);
        when(request.getMethod()).thenReturn(Route.Method.POST.toString());
        when(request.getRequestURI()).thenReturn(path);
        router.route(request, response);
        verify(missingRouteHandler).handle(eq(request), eq(response), any(HandlerChain.class));
    }

    @Test
    public void testRouteMissingDueToPath() throws Exception {
        router.addRoute(Route.Method.GET, path, handler);
        when(request.getMethod()).thenReturn(Route.Method.GET.toString());
        when(request.getRequestURI()).thenReturn(path + "extra");
        when(request.getContextPath()).thenReturn("");
        router.route(request, response);
        verify(missingRouteHandler).handle(eq(request), eq(response), any(HandlerChain.class));
    }

    @Test
    public void testMultipleRouteHandlers() throws Exception {
        RequestResponseHandler h2 = new TestRequestResponseHandler();
        router.addRoute(Route.Method.GET, path, h2, handler);
        when(request.getMethod()).thenReturn(Route.Method.GET.toString());
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        router.route(request, response);
        verify(handler).handle(eq(request), eq(response), any(HandlerChain.class));
    }

    @Test
    public void testRouteOverride() throws Exception {
        RequestResponseHandler handlerOverride = mock(RequestResponseHandler.class);
        router.addRoute(Route.Method.GET, path, handler);
        router.addRoute(Route.Method.GET, path, handlerOverride);
        when(request.getMethod()).thenReturn(Route.Method.GET.toString());
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        router.route(request, response);
        verify(handler, never()).handle(any(HttpServletRequest.class), any(HttpServletResponse.class), any(HandlerChain.class));
        verify(handlerOverride).handle(eq(request), eq(response), any(HandlerChain.class));
    }

    @Test
    public void testExceptionHandler() throws Exception {
        Class<? extends Exception> exClass = ArrayStoreException.class;
        router.addRoute(Route.Method.GET, path, exceptionThrowingHandler);
        router.addExceptionHandler(exClass, new RequestResponseHandler[]{handler});

        when(request.getMethod()).thenReturn(Route.Method.GET.toString());
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        doThrow(exClass).when(exceptionThrowingHandler).handle(eq(request), eq(response), any(HandlerChain.class));
        router.route(request, response);
        verify(handler).handle(eq(request), eq(response), any(HandlerChain.class));
    }

    @Test(expected = ArrayStoreException.class)
    public void testMissingExceptionHandler() throws Exception {
        Class<? extends Exception> exClass = ArrayStoreException.class;
        router.addRoute(Route.Method.GET, path, exceptionThrowingHandler);
        router.addExceptionHandler(RuntimeException.class, new RequestResponseHandler[]{handler});

        when(request.getMethod()).thenReturn(Route.Method.GET.toString());
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        doThrow(exClass).when(exceptionThrowingHandler).handle(eq(request), eq(response), any(HandlerChain.class));
        router.route(request, response);
    }

    @Test(expected = ArrayStoreException.class)
    public void testExceptionInExceptionHandler() throws Exception {
        Class<? extends Exception> exClass = ArrayStoreException.class;
        router.addRoute(Route.Method.GET, path, exceptionThrowingHandler);
        router.addExceptionHandler(exClass, new RequestResponseHandler[]{handler});

        when(request.getMethod()).thenReturn(Route.Method.GET.toString());
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        doThrow(exClass).when(handler).handle(eq(request), eq(response), any(HandlerChain.class));
        doThrow(exClass).when(exceptionThrowingHandler).handle(eq(request), eq(response), any(HandlerChain.class));
        router.route(request, response);
    }
}
