package org.visallo.webster;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RequestResponseHandler extends Handler {
    void handle(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) throws Exception;
}
