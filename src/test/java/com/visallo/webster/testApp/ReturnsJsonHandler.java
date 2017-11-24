package com.visallo.webster.testApp;

import com.visallo.webster.ParameterizedHandler;
import com.visallo.webster.annotations.ContentType;
import com.visallo.webster.annotations.Handle;

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
