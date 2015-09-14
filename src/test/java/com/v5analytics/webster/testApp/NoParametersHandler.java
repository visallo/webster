package com.v5analytics.webster.testApp;

import com.v5analytics.webster.ParameterizedHandler;
import com.v5analytics.webster.annotations.Handle;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class NoParametersHandler implements ParameterizedHandler {
    @Handle
    public String handle() throws IOException {
        return "ok";
    }
}
