package org.visallo.webster;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HandlerChain {
    private final RequestResponseHandler[] handlers;
    private int nextHandlerIndex = 0;

    public HandlerChain(RequestResponseHandler[] handlers) {
        this.handlers = handlers;
    }

    public void next(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (nextHandlerIndex < handlers.length) {
            RequestResponseHandler handler = handlers[nextHandlerIndex];
            nextHandlerIndex++;
            handler.handle(request, response, this);
        }
    }
}
