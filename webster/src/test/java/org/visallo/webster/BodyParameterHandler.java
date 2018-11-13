package org.visallo.webster;

import org.visallo.webster.annotations.Handle;
import org.visallo.webster.annotations.Required;
import org.visallo.webster.annotations.RequestBody;

import javax.servlet.http.HttpServletRequest;

public class BodyParameterHandler implements ParameterizedHandler {
    @Handle
    public void handle(
            HttpServletRequest request,
            @Required(name = "id") String id,
            @RequestBody String body
    ) {
        request.setAttribute("id", id);
        request.setAttribute("body", body);
    }
}
