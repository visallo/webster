package com.v5analytics.webster;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HandlerChain {
    private final Handler[] handlers;
    private int nextHandlerIndex = 0;

    public HandlerChain(Handler[] handlers) {
        this.handlers = handlers;
    }

    public void next(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (nextHandlerIndex < handlers.length) {
            Handler handler = handlers[nextHandlerIndex];
            nextHandlerIndex++;
            handler.handle(request, response, this);
        }
    }
}
