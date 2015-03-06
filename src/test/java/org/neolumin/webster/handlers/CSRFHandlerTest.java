package org.neolumin.webster.handlers;

import org.neolumin.webster.HandlerChain;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.security.SecureRandom;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class CSRFHandlerTest {
    private static final String TOKEN_PARAM = "csrfToken";
    private static final String TOKEN_HEADER = "Csrf-Token-Header";
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HandlerChain chain;
    private CSRFHandler handler;
    private HttpSession session;

    @Before
    public void setup() {
        handler = new CSRFHandler(TOKEN_PARAM, TOKEN_HEADER);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        chain = mock(HandlerChain.class);
    }

    @Test
    public void testGetRequestWithNoCsrfTokenPresent() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(CSRFHandler.CSRF_TOKEN_ATTR)).thenReturn(generateToken());
        handler.handle(request, response, chain);
        verify(chain).next(request, response);
    }

    @Test(expected = CSRFHandler.TokenException.class)
    public void testGetRequestWithCsrfPresent() throws Exception {
        String token = generateToken();
        when(request.getMethod()).thenReturn("GET");
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(CSRFHandler.CSRF_TOKEN_ATTR)).thenReturn(token);
        when(request.getParameter(TOKEN_PARAM)).thenReturn(token);
        handler.handle(request, response, chain);
        verify(chain, never()).next(request, response);
        verify(session).setAttribute(eq(CSRFHandler.CSRF_TOKEN_ATTR), not(eq(token)));
    }

    @Test(expected = CSRFHandler.TokenException.class)
    public void testPostRequestWithNoCsrfTokenInRequest() throws Exception {
        String token = generateToken();
        when(request.getMethod()).thenReturn("POST");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(CSRFHandler.CSRF_TOKEN_ATTR)).thenReturn(token);
        when(request.getParameter(TOKEN_PARAM)).thenReturn(null);
        handler.handle(request, response, chain);
        verify(chain, never()).next(request, response);
    }

    @Test(expected = CSRFHandler.TokenException.class)
    public void testPostRequestWithNoCsrfTokenInSession() throws Exception {
        String token = generateToken();
        when(request.getMethod()).thenReturn("POST");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(CSRFHandler.CSRF_TOKEN_ATTR)).thenReturn(null);
        when(request.getParameter(TOKEN_PARAM)).thenReturn(token);
        handler.handle(request, response, chain);
        verify(chain, never()).next(request, response);
    }

    @Test(expected = CSRFHandler.TokenException.class)
    public void testPostRequestWithNoCsrfTokenInSessionOrRequest() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(CSRFHandler.CSRF_TOKEN_ATTR)).thenReturn(null);
        when(request.getParameter(TOKEN_PARAM)).thenReturn(null);
        handler.handle(request, response, chain);
        verify(chain, never()).next(request, response);
    }

    @Test(expected = CSRFHandler.TokenException.class)
    public void testPostRequestWithNonMatchingCsrfTokens() throws Exception {
        String token = generateToken();
        when(request.getMethod()).thenReturn("POST");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(CSRFHandler.CSRF_TOKEN_ATTR)).thenReturn(token);
        when(request.getParameter(TOKEN_PARAM)).thenReturn(token + "a");
        handler.handle(request, response, chain);
        verify(chain, never()).next(request, response);
    }

    @Test
    public void testPostRequestWithMatchingCsrfTokensInParameter() throws Exception {
        String token = generateToken();
        when(request.getMethod()).thenReturn("POST");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(CSRFHandler.CSRF_TOKEN_ATTR)).thenReturn(token);
        when(request.getHeader(TOKEN_HEADER)).thenReturn(token + "a");
        when(request.getParameter(TOKEN_PARAM)).thenReturn(token);
        handler.handle(request, response, chain);
        verify(chain).next(request, response);
    }

    @Test
    public void testPostRequestWithMatchingCsrfTokensInHeader() throws Exception {
        String token = generateToken();
        when(request.getMethod()).thenReturn("POST");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(CSRFHandler.CSRF_TOKEN_ATTR)).thenReturn(token);
        when(request.getParameter(TOKEN_PARAM)).thenReturn(null);
        when(request.getHeader(TOKEN_HEADER)).thenReturn(token);
        handler.handle(request, response, chain);
        verify(chain).next(request, response);
    }

    private String generateToken() {
        return new BigInteger(120, new SecureRandom()).toString(32);
    }
}
