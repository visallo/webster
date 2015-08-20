package com.v5analytics.webster;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class RouteTest {
    private String path;
    private RequestResponseHandler handler;

    @Before
    public void before() {
        handler = mock(RequestResponseHandler.class);
        path = "/test";
    }

    @Test
    public void testRouteMiss() {
        Route r = new Route(Route.Method.GET, path, handler);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/foo");
        when(request.getContextPath()).thenReturn("");
        assertFalse(r.isMatch(request));
    }

    @Test
    public void testExactRouteMatch() {
        Route r = new Route(Route.Method.GET, path, handler);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(path);
        when(request.getContextPath()).thenReturn("");
        assertTrue(r.isMatch(request));
    }

    @Test
    public void testRouteMatchWithComponents() {
        Route r = new Route(Route.Method.GET, path + "/{id}", handler);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(path + "/25");
        when(request.getContextPath()).thenReturn("");
        assertTrue(r.isMatch(request));
        verify(request).setAttribute("id", "25");
    }

    @Test
    public void testComplexComponentAttributeSetting() {
        Route r = new Route(Route.Method.GET, path + "/{model}/edit/{_id}", handler);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(path + "/person/edit/25");
        when(request.getContextPath()).thenReturn("");
        assertTrue(r.isMatch(request));
        verify(request).setAttribute("model", "person");
        verify(request).setAttribute("_id", "25");
    }

    @Test
    public void testWithEscapedSlash() {
        Route r = new Route(Route.Method.GET, path + "/{id}/test", handler);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(path + "/12%2F34/test");
        when(request.getContextPath()).thenReturn("");
        assertTrue(r.isMatch(request));
        verify(request).setAttribute("id", "12/34");
    }

    @Test
    public void testComponentAsBaseFilename() {
        Route r = new Route(Route.Method.GET, path + "/{file}.ext", handler);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(path + "/less.ext");
        when(request.getContextPath()).thenReturn("");
        assertTrue(r.isMatch(request));
        verify(request).setAttribute("file", "less");
    }

    public void testWithRegexSpecialCharacters() {
        Route r = new Route(Route.Method.GET, path + "\\^$.|?*+()[]", handler);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(path + "\\^$.|?*+()[]");
        when(request.getContextPath()).thenReturn("");
        assertTrue(r.isMatch(request));
    }

    @Test
    public void testRestStyleUrlWithSpecialCharactersAndKnownTail() {
        Route r = new Route(Route.Method.GET, path + "/{resourceName}/data.json", handler);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(path + "/test@test.com/other/data.json");
        when(request.getContextPath()).thenReturn("");
        assertTrue(r.isMatch(request));
        verify(request).setAttribute("resourceName", "test@test.com/other");
    }

    @Test
    public void testRestStyleUrlWithSpecialCharacters() {
        Route r = new Route(Route.Method.GET, path + "/{resourceName*}/{file}.{ext}", handler);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(path + "/test@test.com/other/less.ext");
        when(request.getContextPath()).thenReturn("");
        assertTrue(r.isMatch(request));
        verify(request).setAttribute("resourceName", "test@test.com/other");
        verify(request).setAttribute("file", "less");
        verify(request).setAttribute("ext", "ext");
    }

    @Test
    public void testRegexRoute() {
        Route r = new Route(Route.Method.GET, path + "/{resourceName<[0-9]*>}/end", handler);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(path + "/1283723/end");
        when(request.getContextPath()).thenReturn("");
        assertTrue(r.isMatch(request));
        verify(request).setAttribute("resourceName", "1283723");
    }

    @Test
    public void testRegexRouteNoMatch() {
        Route r = new Route(Route.Method.GET, path + "/{resourceName<[0-9]*>}/end", handler);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(path + "/128a3723/end");
        when(request.getContextPath()).thenReturn("");
        assertFalse(r.isMatch(request));
    }
}
