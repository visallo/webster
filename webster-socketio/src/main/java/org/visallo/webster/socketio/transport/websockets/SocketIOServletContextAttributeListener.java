package org.visallo.webster.socketio.transport.websockets;

import com.codeminders.socketio.server.transport.websocket.WebsocketTransportConnection;
import org.visallo.webster.socketio.WebsterSocketIOException;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;

public class SocketIOServletContextAttributeListener implements ServletContextAttributeListener {
    @Override
    public void attributeAdded(ServletContextAttributeEvent event) {
        if (event.getValue() instanceof ServerContainer) {
            ServerContainer serverContainer = (ServerContainer) event.getValue();
            try {
                serverContainer.addEndpoint(WebsocketTransportConnection.class);
            } catch (DeploymentException ex) {
                throw new WebsterSocketIOException("could not add endpoint", ex);
            }
        }
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent event) {
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent event) {
    }
}
