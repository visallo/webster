package com.v5analytics.webster.resultWriters;

import com.v5analytics.webster.HandlerChain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ResultWriter {
    void write(Object result, HttpServletRequest request, HttpServletResponse response, HandlerChain chain) throws Exception;
}
