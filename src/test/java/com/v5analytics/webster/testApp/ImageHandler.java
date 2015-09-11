package com.v5analytics.webster.testApp;

import com.v5analytics.webster.ParameterizedHandler;
import com.v5analytics.webster.annotations.Handle;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class ImageHandler implements ParameterizedHandler {
    @Handle
    public void handle(HttpServletResponse response) throws IOException {
        byte[] data = new byte[1000];
        int read;
        InputStream in = ImageHandler.class.getResourceAsStream("webster.jpg");
        ServletOutputStream out = response.getOutputStream();
        response.setContentType("image/jpg");
        while ((read = in.read(data)) > 0) {
            out.write(data, 0, read);
        }
    }
}
