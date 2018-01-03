package org.visallo.webster.testApp;

import org.visallo.webster.ParameterizedHandler;
import org.visallo.webster.annotations.Handle;

import java.io.IOException;

public class NoParametersHandler implements ParameterizedHandler {
    @Handle
    public String handle() throws IOException {
        return "ok";
    }
}
