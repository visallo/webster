package org.visallo.webster;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestRequestResponseHandler implements RequestResponseHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) throws Exception {
        request.setAttribute("handled", "true");
        chain.next(request, response);
    }
}
