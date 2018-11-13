package org.visallo.webster.testApp;

import org.visallo.webster.ParameterizedHandler;
import org.visallo.webster.annotations.ContentType;
import org.visallo.webster.annotations.Handle;

public class ReturnsJsonHandler implements ParameterizedHandler {
    @Handle
    @ContentType("text/json")
    public String handle() {
        return "{\"key1\":\"value1\"}";
    }
}
