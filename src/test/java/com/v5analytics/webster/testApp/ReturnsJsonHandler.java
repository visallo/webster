package com.v5analytics.webster.testApp;

import com.v5analytics.webster.ParameterizedHandler;
import com.v5analytics.webster.annotations.Handle;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ReturnsJsonHandler implements ParameterizedHandler {
    @Handle
    public void handle(
            HttpServletResponse response
    ) throws IOException {
        response.setContentType("text/json");
        response.getOutputStream().print("{\"key1\":\"value1\"}");
    }
}
