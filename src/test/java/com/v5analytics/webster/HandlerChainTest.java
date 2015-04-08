package com.v5analytics.webster;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class HandlerChainTest {

    @Test
    public void testHandlerInvocation() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Handler[] handlers = { new TestHandler(), new TestHandler() };
        HandlerChain chain = new HandlerChain(handlers);
        chain.next(request, response);
        verify(request, times(2)).setAttribute(anyString(), anyObject());
    }
}
