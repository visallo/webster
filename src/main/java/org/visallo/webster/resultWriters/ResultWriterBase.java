package org.visallo.webster.resultWriters;

import org.visallo.webster.HandlerChain;
import org.visallo.webster.annotations.ContentType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

public class ResultWriterBase implements ResultWriter {
    private final String contentType;
    private final boolean voidReturn;

    public ResultWriterBase(Method handleMethod) {
        contentType = getContentType(handleMethod);
        voidReturn = handleMethod.getReturnType().equals(Void.TYPE) || handleMethod.getReturnType().equals(Void.class);
    }

    protected String getContentType(Method handleMethod) {
        ContentType contentTypeAnnotation = handleMethod.getAnnotation(ContentType.class);
        if (contentTypeAnnotation != null) {
            return contentTypeAnnotation.value();
        } else {
            return null;
        }
    }

    @Override
    public void write(Object result, HttpServletRequest request, HttpServletResponse response, HandlerChain chain) throws Exception {
        if (contentType != null) {
            response.setContentType(contentType);
        }
        if (voidReturn) {
            return;
        }
        if (result == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        writeResult(request, response, result);
    }

    @SuppressWarnings("unused")
    protected void writeResult(HttpServletRequest request, HttpServletResponse response, Object result) throws IOException {
        response.getOutputStream().write(getResultBytes(result));
    }

    protected byte[] getResultBytes(Object result) {
        if (result.getClass() == byte[].class) {
            return (byte[]) result;
        }
        return result.toString().getBytes();
    }
}
