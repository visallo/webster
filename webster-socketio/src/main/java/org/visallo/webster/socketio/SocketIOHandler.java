package org.visallo.webster.socketio;

import com.codeminders.socketio.server.SocketIOManager;
import com.codeminders.socketio.server.Transport;
import com.codeminders.socketio.server.TransportProvider;
import org.visallo.webster.ParameterizedHandler;
import org.visallo.webster.annotations.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SocketIOHandler implements ParameterizedHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketIOHandler.class);

    @Handle
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            TransportProvider transportProvider = SocketIOManager.getInstance().getTransportProvider();
            Transport transport = transportProvider.getTransport(request);
            transport.handle(request, response, SocketIOManager.getInstance());
        } catch (Exception ex) {
            LOGGER.warn("could not complete request", ex);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
