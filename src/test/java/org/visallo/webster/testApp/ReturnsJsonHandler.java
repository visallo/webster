package org.visallo.webster.testApp;

import org.visallo.webster.ParameterizedHandler;
import org.visallo.webster.annotations.ContentType;
import org.visallo.webster.annotations.Handle;

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
