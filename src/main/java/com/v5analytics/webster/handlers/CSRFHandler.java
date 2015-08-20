package com.v5analytics.webster.handlers;

import com.v5analytics.webster.HandlerChain;
import com.v5analytics.webster.RequestResponseHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.security.SecureRandom;

public class CSRFHandler implements RequestResponseHandler {
    public static final String CSRF_TOKEN_ATTR = "webster.csrf.token";

    private final String tokenRequestParameterName;
    private final String tokenRequestHeaderName;

    public static String getSavedToken(HttpServletRequest request) {
        return getSavedToken(request, false);
    }

    public static String getSavedToken(HttpServletRequest request, boolean createTokenIfMissing) {
        HttpSession session = request.getSession(createTokenIfMissing);
        if (session == null) {
            return null;
        }

        String token = (String) session.getAttribute(CSRF_TOKEN_ATTR);
        if (token == null && createTokenIfMissing) {
            token = resetSavedToken(request);
        }

        return token;
    }

    private static String resetSavedToken(HttpServletRequest request) {
        String token = new BigInteger(120, new SecureRandom()).toString(32);
        request.getSession().setAttribute(CSRF_TOKEN_ATTR, token);
        return token;
    }

    private static void clearSavedToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            request.getSession().removeAttribute(CSRF_TOKEN_ATTR);
        }
    }

    public CSRFHandler(String tokenRequestParameterName) {
        this.tokenRequestParameterName = tokenRequestParameterName;
        this.tokenRequestHeaderName = null;
    }

    public CSRFHandler(String tokenRequestParameterName, String tokenRequestHeaderName) {
        this.tokenRequestParameterName = tokenRequestParameterName;
        this.tokenRequestHeaderName = tokenRequestHeaderName;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) throws Exception {
        if (request.getMethod().equals("GET")) {
            verifyTokenAbsence(request);
        } else {
            verifyToken(request, response);
        }
        chain.next(request, response);
    }

    private void verifyToken(HttpServletRequest request, HttpServletResponse response) throws TokenException {
        String tokenFromRequest = getTokenFromRequest(request);
        String storedToken = CSRFHandler.getSavedToken(request);
        if (tokenFromRequest == null) {
            throw new TokenException("CSRF token not found in request parameter " + this.tokenRequestParameterName);
        }
        if (storedToken == null) {
            throw new TokenException("CSRF token has not been set in the user's session");
        }
        if (!tokenFromRequest.equals(storedToken)) {
            throw new TokenException("CSRF token from request parameter " + this.tokenRequestParameterName
                    + " does not match the one stored in the user's session");
        }
    }

    private void verifyTokenAbsence(HttpServletRequest request) throws TokenException {
        String tokenFromRequest = getTokenFromRequest(request);
        if (tokenFromRequest != null) {
            CSRFHandler.resetSavedToken(request);
            throw new TokenException("CSRF token found in a " + request.getMethod() + " request. Token has been reset");
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String token = null;

        if (this.tokenRequestParameterName != null) {
            token = request.getParameter(this.tokenRequestParameterName);
        }

        if (token == null && this.tokenRequestHeaderName != null) {
            token = request.getHeader(this.tokenRequestHeaderName);
        }

        return token;
    }

    public class TokenException extends Exception {
        public TokenException(String msg) {
            super(msg);
        }
    }
}
