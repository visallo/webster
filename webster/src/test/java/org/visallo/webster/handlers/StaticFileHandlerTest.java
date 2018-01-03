package org.visallo.webster.handlers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.visallo.webster.HandlerChain;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class StaticFileHandlerTest {
    @Test
    public void testStaticFileHandler() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletContext servletContext = mock(ServletContext.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        HandlerChain chain = new HandlerChain(null);

        when(servletContext.getNamedDispatcher("default")).thenReturn(dispatcher);

        StaticFileHandler handler = new StaticFileHandler(servletContext);
        handler.handle(request, response, chain);

        verify(servletContext).getNamedDispatcher("default");
        verify(dispatcher).forward(any(HttpServletRequest.class), eq(response));
    }
}
