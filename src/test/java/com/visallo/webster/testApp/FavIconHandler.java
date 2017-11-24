package com.visallo.webster.testApp;

import com.visallo.webster.HandlerChain;
import com.visallo.webster.RequestResponseHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FavIconHandler implements RequestResponseHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, HandlerChain chain) throws Exception {

    }
}
