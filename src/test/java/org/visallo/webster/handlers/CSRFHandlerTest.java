package org.visallo.webster.handlers;

import org.visallo.webster.HandlerChain;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatcher;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.security.SecureRandom;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class CSRFHandlerTest {
    private static final String TOKEN_PARAM = "csrfToken";
    private static final String TOKEN_HEADER = "Csrf-Token-Header";
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HandlerChain chain;
    private CSRFHandler handler;

    @Before
    public void setup() {
        handler = new CSRFHandler(TOKEN_PARAM, TOKEN_HEADER);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(HandlerChain.class);
    }

    @Test
    public void testGetRequestWithNoCsrfTokenPresent() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getCookies()).thenReturn(new Cookie[] { generateTokenCookie() });
        handler.handle(request, response, chain);
        verify(chain).next(request, response);
    }

    @Test
    public void testGetRequestWithCsrfPresent() throws Exception {
        Cookie token = generateTokenCookie();
        when(request.getMethod()).thenReturn("GET");
        when(request.getCookies()).thenReturn(new Cookie[] { token });
        when(request.getParameter(TOKEN_PARAM)).thenReturn(token.getValue());
        try {
            handler.handle(request, response, chain);
            fail("Expected " + CSRFHandler.TokenException.class.getName());
        } catch (CSRFHandler.TokenException ex) {
            // expected
        }
        verify(chain, never()).next(request, response);
        verify(response).addCookie(argThat(new CookieMatcher(token, false)));
    }

    @Test
    public void testPostRequestWithNoCsrfTokenInRequest() throws Exception {
        Cookie token = generateTokenCookie();
        when(request.getMethod()).thenReturn("POST");
        when(request.getCookies()).thenReturn(new Cookie[] { token });
        when(request.getParameter(TOKEN_PARAM)).thenReturn(null);
        try {
            handler.handle(request, response, chain);
            fail("Expected " + CSRFHandler.TokenException.class.getName());
        } catch (CSRFHandler.TokenException ex) {
            // expected
        }
        verify(chain, never()).next(request, response);
    }

    @Test
    public void testPostRequestWithNoCsrfTokenInSession() throws Exception {
        Cookie token = generateTokenCookie();
        when(request.getMethod()).thenReturn("POST");
        when(request.getCookies()).thenReturn(null);
        when(request.getParameter(TOKEN_PARAM)).thenReturn(token.getValue());
        try {
            handler.handle(request, response, chain);
            fail("Expected " + CSRFHandler.TokenException.class.getName());
        } catch (CSRFHandler.TokenException ex) {
            // expected
        }
        verify(chain, never()).next(request, response);
    }

    @Test
    public void testPostRequestWithNoCsrfTokenInSessionOrRequest() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getCookies()).thenReturn(null);
        when(request.getParameter(TOKEN_PARAM)).thenReturn(null);
        try {
            handler.handle(request, response, chain);
            fail("Expected " + CSRFHandler.TokenException.class.getName());
        } catch (CSRFHandler.TokenException ex) {
            // expected
        }
        verify(chain, never()).next(request, response);
    }

    @Test
    public void testPostRequestWithNonMatchingCsrfTokens() throws Exception {
        Cookie token = generateTokenCookie();
        when(request.getMethod()).thenReturn("POST");
        when(request.getCookies()).thenReturn(new Cookie[] { token });
        when(request.getParameter(TOKEN_PARAM)).thenReturn(token.getValue() + "a");
        try {
            handler.handle(request, response, chain);
            fail("Expected " + CSRFHandler.TokenException.class.getName());
        } catch (CSRFHandler.TokenException ex) {
            // expected
        }
        verify(chain, never()).next(request, response);
    }

    @Test
    public void testPostRequestWithMatchingCsrfTokensInParameter() throws Exception {
        Cookie token = generateTokenCookie();
        when(request.getMethod()).thenReturn("POST");
        when(request.getCookies()).thenReturn(new Cookie[] { token });
        when(request.getHeader(TOKEN_HEADER)).thenReturn(token.getValue() + "a");
        when(request.getParameter(TOKEN_PARAM)).thenReturn(token.getValue());
        handler.handle(request, response, chain);
        verify(chain).next(request, response);
    }

    @Test
    public void testPostRequestWithMatchingCsrfTokensInHeader() throws Exception {
        Cookie token = generateTokenCookie();
        when(request.getMethod()).thenReturn("POST");
        when(request.getCookies()).thenReturn(new Cookie[] { token });
        when(request.getParameter(TOKEN_PARAM)).thenReturn(null);
        when(request.getHeader(TOKEN_HEADER)).thenReturn(token.getValue());
        handler.handle(request, response, chain);
        verify(chain).next(request, response);
    }

    private Cookie generateTokenCookie() {
        String token = new BigInteger(120, new SecureRandom()).toString(32);
        return new Cookie(CSRFHandler.CSRF_COOKIE_NAME, token);
    }

    private class CookieMatcher extends ArgumentMatcher<Cookie> {

        private Cookie left;
        private boolean shouldEqual;

        public CookieMatcher(Cookie cookie, boolean shouldEqual) {
            this.left = cookie;
            this.shouldEqual = shouldEqual;
        }

        @Override
        public boolean matches(Object object) {
            if (object instanceof Cookie) {
                Cookie right = (Cookie) object;
                boolean equal = left.getName().equals(right.getName())
                        && left.getValue().equals(right.getValue());
                return shouldEqual ? equal : !equal;
            }

            return false;
        }
    }
}
