package com.v5analytics.webster;

import com.v5analytics.webster.annotations.Handle;
import com.v5analytics.webster.annotations.Optional;
import com.v5analytics.webster.annotations.Required;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestParameterizedHandler implements ParameterizedHandler {
    @Handle
    public void doIt(
            HttpServletRequest request,
            HttpServletResponse response,
            HandlerChain chain,
            @Required(name = "requiredInt") int requiredInt,
            @Optional(name = "optionalIntWithDefault", defaultValue = "42") int optionalIntWithDefault,
            @Optional(name = "optionalInteger") Integer optionalInteger,
            @Optional(name = "optionalIntegerWithDefault", defaultValue = "42") Integer optionalIntegerWithDefault,
            @Required(name = "requiredString") String requiredString,
            @Optional(name = "optionalStringWithDefault", defaultValue = "default value") String optionalStringWithDefault,
            @Required(name = "testParameterObject") TestParameterObject testParameterObject,
            TestUser user
    ) {
        request.setAttribute("handled", "true");
        request.setAttribute("requiredInt", requiredInt);
        request.setAttribute("optionalIntWithDefault", optionalIntWithDefault);
        request.setAttribute("optionalInteger", optionalInteger);
        request.setAttribute("optionalIntegerWithDefault", optionalIntegerWithDefault);
        request.setAttribute("requiredString", requiredString);
        request.setAttribute("optionalStringWithDefault", optionalStringWithDefault);
        request.setAttribute("testParameterObject", testParameterObject);
        request.setAttribute("user", user);
    }
}
