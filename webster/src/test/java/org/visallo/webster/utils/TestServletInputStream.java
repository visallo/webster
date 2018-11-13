package org.visallo.webster.utils;

import org.visallo.webster.WebsterException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestServletInputStream extends ServletInputStream {
    private final InputStream in;

    public TestServletInputStream(InputStream in) {
        this.in = in;
    }

    public TestServletInputStream(String string) {
        this(new ByteArrayInputStream(string.getBytes()));
    }

    @Override
    public boolean isFinished() {
        throw new WebsterException("not implemented");
    }

    @Override
    public boolean isReady() {
        throw new WebsterException("not implemented");
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        throw new WebsterException("not implemented");
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }
}
