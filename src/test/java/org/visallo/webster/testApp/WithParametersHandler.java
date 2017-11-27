package org.visallo.webster.testApp;

import org.visallo.webster.ParameterizedHandler;
import org.visallo.webster.TestParameterObject;
import org.visallo.webster.annotations.ContentType;
import org.visallo.webster.annotations.Handle;
import org.visallo.webster.annotations.Optional;
import org.visallo.webster.annotations.Required;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WithParametersHandler implements ParameterizedHandler {
    @Handle
    @ContentType("text/plain")
    public void handle(
            @Required(name = "requiredInt") int requiredInt,
            @Optional(name = "optionalIntWithDefault", defaultValue = "42") int optionalIntWithDefault,
            @Optional(name = "optionalInteger") Integer optionalInteger,
            @Optional(name = "optionalIntegerWithDefault", defaultValue = "42") Integer optionalIntegerWithDefault,
            @Required(name = "requiredString") String requiredString,
            @Optional(name = "optionalStringWithDefault", defaultValue = "default value") String optionalStringWithDefault,
            @Required(name = "testParameterObject") TestParameterObject testParameterObject,
            HttpServletResponse response
    ) throws IOException {
        response.getOutputStream().print("requiredInt=" + requiredInt + "\n");
        response.getOutputStream().print("optionalIntWithDefault=" + optionalIntWithDefault + "\n");
        response.getOutputStream().print("optionalInteger=" + optionalInteger + "\n");
        response.getOutputStream().print("optionalIntegerWithDefault=" + optionalIntegerWithDefault + "\n");
        response.getOutputStream().print("requiredString=" + requiredString + "\n");
        response.getOutputStream().print("optionalStringWithDefault=" + optionalStringWithDefault + "\n");
        response.getOutputStream().print("testParameterObject=" + testParameterObject + "\n");
        response.getOutputStream().print("ok");
    }
}
