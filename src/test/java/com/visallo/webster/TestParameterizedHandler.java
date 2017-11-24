package com.visallo.webster;

import com.visallo.webster.annotations.Handle;
import com.visallo.webster.annotations.Optional;
import com.visallo.webster.annotations.Required;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestParameterizedHandler implements ParameterizedHandler {
    @Handle
    public String doIt(
            HttpServletRequest request,
            HttpServletResponse response,
            HandlerChain chain,
            App app,
            Router router,
            @Required(name = "requiredBoolean") boolean requiredBoolean,
            @Optional(name = "optionalBooleanWithDefault", defaultValue = "false") boolean optionalBooleanWithDefault,
            @Required(name = "requiredInt") int requiredInt,
            @Optional(name = "optionalIntWithDefault", defaultValue = "42") int optionalIntWithDefault,
            @Optional(name = "optionalInteger") Integer optionalInteger,
            @Optional(name = "optionalIntegerWithDefault", defaultValue = "42") Integer optionalIntegerWithDefault,
            @Required(name = "requiredString") String requiredString,
            @Optional(name = "optionalStringWithDefault", defaultValue = "default value") String optionalStringWithDefault,
            @Required(name = "requiredStringArray[]") String[] requiredStringArray,
            @Optional(name = "optionalStringArray[]") String[] optionalStringArray,
            @Required(name = "testParameterObject") TestParameterObjectExtended testParameterObject,
            @Required(name = "requiredStringInHeader") String requiredStringInHeader,
            @Required(name = "requiredBooleanArray[]") Boolean[] requiredBooleanArray,
            @Required(name = "requiredIntegerArray[]") Integer[] requiredIntegerArray,
            TestUser user
    ) {
        request.setAttribute("handled", "true");
        request.setAttribute("requiredBoolean", requiredBoolean);
        request.setAttribute("optionalBooleanWithDefault", optionalBooleanWithDefault);
        request.setAttribute("requiredInt", requiredInt);
        request.setAttribute("optionalIntWithDefault", optionalIntWithDefault);
        request.setAttribute("optionalInteger", optionalInteger);
        request.setAttribute("optionalIntegerWithDefault", optionalIntegerWithDefault);
        request.setAttribute("requiredString", requiredString);
        request.setAttribute("optionalStringWithDefault", optionalStringWithDefault);
        request.setAttribute("testParameterObject", testParameterObject);
        request.setAttribute("requiredStringArray", requiredStringArray);
        request.setAttribute("optionalStringArray", optionalStringArray);
        request.setAttribute("requiredStringInHeader", requiredStringInHeader);
        request.setAttribute("requiredBooleanArray", requiredBooleanArray);
        request.setAttribute("requiredIntegerArray", requiredIntegerArray);
        request.setAttribute("user", user);

        return "OK";
    }
}
