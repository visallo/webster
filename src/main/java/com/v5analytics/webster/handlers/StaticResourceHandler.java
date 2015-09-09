package com.v5analytics.webster.handlers;

import com.v5analytics.webster.HandlerChain;
import com.v5analytics.webster.RequestResponseHandler;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StaticResourceHandler implements RequestResponseHandler {
    private final String pathInfo;
    private Class classRef;
    private String contentType;

    public StaticResourceHandler(Class classRef, String pathInfo, String contentType) {
        this.classRef = classRef;
        this.pathInfo = pathInfo;
        this.contentType = contentType;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) throws Exception {
        InputStream in = classRef.getResourceAsStream(pathInfo);
        if (in == null) {
            throw new IOException("Could not find resource at: " + pathInfo);
        }
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
