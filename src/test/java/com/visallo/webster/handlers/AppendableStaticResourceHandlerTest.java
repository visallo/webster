package com.visallo.webster.handlers;

import com.visallo.webster.HandlerChain;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class AppendableStaticResourceHandlerTest {
    private AppendableStaticResourceHandler handler;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HandlerChain chain;

    @Before
    public void setup() {
        handler = new AppendableStaticResourceHandler("application/javascript");
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(HandlerChain.class);
    }

    @Test
    public void testWithNoResourceFiles() throws Exception {
        handler.handle(request, response, chain);
        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void testSingleResourceFile() throws Exception {
        CapturingServletOutputStream stream = new CapturingServletOutputStream();
        when(response.getOutputStream()).thenReturn(stream);
        handler.appendResource("/top-level.js");
        handler.handle(request, response, chain);
        verify(response).setContentType("application/javascript");
        assertEquals("var top = true;\n", stream.toString());
    }

    @Test
    public void testMultipleResourceFiles() throws Exception {
        CapturingServletOutputStream stream = new CapturingServletOutputStream();
        when(response.getOutputStream()).thenReturn(stream);
        handler.appendResource("/top-level.js");
        handler.appendResource("/subdir/second-level.js");
        handler.handle(request, response, chain);
        verify(response).setContentType("application/javascript");
        assertEquals("var top = true;\n" +
                "var second = true;\n", stream.toString());
    }

    private class CapturingServletOutputStream extends ServletOutputStream {
        private List bytes = new ArrayList<Integer>();
        private WriteListener writeListener;

        @Override
        public void write(int b) throws IOException {
            bytes.add(b);
        }

        @Override
        public String toString() {
            byte[] strBytes = new byte[bytes.size()];
            for (int i = 0; i < bytes.size(); i++) {
                strBytes[i] = (byte) ((Integer) bytes.get(i)).intValue();
            }
            return new String(strBytes);
        }

        @Override
        public boolean isReady() {
            return writeListener != null;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            this.writeListener = writeListener;
        }
    }
}
