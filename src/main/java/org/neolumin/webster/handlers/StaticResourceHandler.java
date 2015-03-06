package org.neolumin.webster.handlers;

import org.neolumin.webster.Handler;
import org.neolumin.webster.HandlerChain;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StaticResourceHandler implements Handler {
    private final String pathInfo;
    private Class classRef;
    private String contentType;

    public StaticResourceHandler(Class classRef, String pathInfo, String contentType) {
        this.classRef = classRef;
        this.pathInfo = pathInfo;
        this.contentType = contentType;
    }

    public void handle(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) throws Exception {
        InputStream in = classRef.getResourceAsStream(pathInfo);
        ServletOutputStream out = response.getOutputStream();
        response.setContentType(contentType);
        copy(in, out);
        out.close();
    }

    private void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) > 0) {
            out.write(buffer, 0, read);
        }
    }
}
