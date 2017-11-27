package org.visallo.webster.handlers;

import org.visallo.webster.HandlerChain;
import org.visallo.webster.RequestResponseHandler;
import org.visallo.webster.WebsterException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class AppendableStaticResourceHandler implements RequestResponseHandler {
    private final String contentType;
    private final List<String> resourcePaths = new ArrayList<String>();

    public AppendableStaticResourceHandler(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) throws Exception {
        if (resourcePaths.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType(contentType);
        ServletOutputStream out = response.getOutputStream();
        for (String resourcePath : resourcePaths) {
            InputStream in = this.getClass().getResourceAsStream(resourcePath);
            if (in == null) {
                throw new WebsterException("Could not find resource on classpath: " + resourcePath);
            }
            copy(in, out);
            out.write("\n".getBytes());
            in.close();
        }
        out.close();
    }

    public void appendResource(String pathInfo) {
        resourcePaths.add(pathInfo);
    }

    private void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) > 0) {
            out.write(buffer, 0, read);
        }
    }
}
