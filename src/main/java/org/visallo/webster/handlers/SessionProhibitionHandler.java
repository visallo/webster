package org.visallo.webster.handlers;

import org.visallo.webster.HandlerChain;
import org.visallo.webster.RequestResponseHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SessionProhibitionHandler implements RequestResponseHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) throws Exception {
        chain.next(new SessionProhibitionHttpRequestWrapper(request), response);
    }

    private class SessionProhibitionHttpRequestWrapper extends HttpServletRequestWrapper {
        public static final String ERROR_MSG = "javax.servlet.http.HttpSession use is prohibited";

        public SessionProhibitionHttpRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public HttpSession getSession(boolean create) {
            throw new UnsupportedOperationException(ERROR_MSG);
        }

        @Override
        public HttpSession getSession() {
            throw new UnsupportedOperationException(ERROR_MSG);
        }

        @Override
        public String getRequestedSessionId() {
            throw new UnsupportedOperationException(ERROR_MSG);
        }

        @Override
        public boolean isRequestedSessionIdValid() {
            throw new UnsupportedOperationException(ERROR_MSG);
        }

        @Override
        public boolean isRequestedSessionIdFromCookie() {
            throw new UnsupportedOperationException(ERROR_MSG);
        }

        @Override
        public boolean isRequestedSessionIdFromURL() {
            throw new UnsupportedOperationException(ERROR_MSG);
        }

        @Override
        public boolean isRequestedSessionIdFromUrl() {
            throw new UnsupportedOperationException(ERROR_MSG);
        }

        @Override
        public String changeSessionId() {
            throw new UnsupportedOperationException(ERROR_MSG);
        }
    }

}
