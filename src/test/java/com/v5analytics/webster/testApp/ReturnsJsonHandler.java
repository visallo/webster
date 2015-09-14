package com.v5analytics.webster.testApp;

import com.v5analytics.webster.ParameterizedHandler;
import com.v5analytics.webster.annotations.ContentType;
import com.v5analytics.webster.annotations.Handle;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ReturnsJsonHandler implements ParameterizedHandler {
    @Handle
    @ContentType("text/json")
    public String handle(
            HttpServletResponse response
    ) throws IOException {
        return "{\"key1\":\"value1\"}";
    }
}
