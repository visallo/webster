package com.visallo.webster.handlers;

import com.visallo.webster.HandlerChain;
import com.visallo.webster.RequestResponseHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.mock;

@RunWith(JUnit4.class)
public class SessionProhibitionHandlerTest {
    private SessionProhibitionHandler handler;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @Before
    public void setup() {
        handler = new SessionProhibitionHandler();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetSessionIsProhibited() throws Exception {
        RequestResponseHandler nextHandler = (request, response, chain) -> {
            request.getSession();
        };
        handler.handle(request, response, chain(nextHandler));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetSessionWithArgIsProhibited() throws Exception {
        RequestResponseHandler nextHandler = (request, response, chain) -> {
            request.getSession(true);
        };
        handler.handle(request, response, chain(nextHandler));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetRequestSessionIdIsProhibited() throws Exception {
        RequestResponseHandler nextHandler = (request, response, chain) -> {
            request.getRequestedSessionId();
        };
        handler.handle(request, response, chain(nextHandler));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIsRequestedSessionIdValidIsProhibited() throws Exception {
        RequestResponseHandler nextHandler = (request, response, chain) -> {
            request.isRequestedSessionIdValid();
        };
        handler.handle(request, response, chain(nextHandler));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIsRequestedSessionIdFromCookieIsProhibited() throws Exception {
        RequestResponseHandler nextHandler = (request, response, chain) -> {
            request.isRequestedSessionIdFromCookie();
        };
        handler.handle(request, response, chain(nextHandler));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIsRequestedSessionIdFromUrlIsProhibited() throws Exception {
        RequestResponseHandler nextHandler = (request, response, chain) -> {
            request.isRequestedSessionIdFromUrl();
        };
        handler.handle(request, response, chain(nextHandler));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIsRequestedSessionIdFromURLIsProhibited() throws Exception {
        RequestResponseHandler nextHandler = (request, response, chain) -> {
            request.isRequestedSessionIdFromURL();
        };
        handler.handle(request, response, chain(nextHandler));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testChangeSessionIdIsProhibited() throws Exception {
        RequestResponseHandler nextHandler = (request, response, chain) -> {
            request.changeSessionId();
        };
        handler.handle(request, response, chain(nextHandler));
    }

    public HandlerChain chain(RequestResponseHandler handler) {
        return new HandlerChain(new RequestResponseHandler[] { handler });
    }
}
