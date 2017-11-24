package com.visallo.webster.testApp;

import com.visallo.webster.ParameterizedHandler;
import com.visallo.webster.annotations.Handle;

import java.io.IOException;

public class NoParametersHandler implements ParameterizedHandler {
    @Handle
    public String handle() throws IOException {
        return "ok";
    }
}
