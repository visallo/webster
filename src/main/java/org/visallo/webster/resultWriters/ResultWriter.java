package org.visallo.webster.resultWriters;

import org.visallo.webster.HandlerChain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ResultWriter {
    void write(Object result, HttpServletRequest request, HttpServletResponse response, HandlerChain chain) throws Exception;
}
