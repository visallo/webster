package org.visallo.webster.testApp;

import org.visallo.webster.ParameterizedHandler;
import org.visallo.webster.annotations.ContentType;
import org.visallo.webster.annotations.Handle;
import org.visallo.webster.annotations.Required;
import org.visallo.webster.annotations.RequestBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.stream.Collectors;

public class WithInputStreamBody implements ParameterizedHandler {
    @Handle
    @ContentType("text/plain")
    public void handle(
            @Required(name = "id") String id,
            @RequestBody InputStream in,
            HttpServletResponse response
    ) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(in)));
        ServletOutputStream out = response.getOutputStream();
        out.println("id: " + id);
        out.println("BEGIN body");
        for (String line : reader.lines().collect(Collectors.toList())) {
            out.println(line);
        }
        out.println("END body");
    }
}
