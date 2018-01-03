package org.visallo.webster.socketio;

import com.codeminders.socketio.server.Namespace;
import com.codeminders.socketio.server.SocketIOManager;
import com.codeminders.socketio.server.TransportProvider;
import com.codeminders.socketio.server.transport.websocket.ServletConfigHolder;
import com.codeminders.socketio.server.transport.websocket.WebsocketTransportConnection;
import com.codeminders.socketio.server.transport.websocket.WebsocketTransportProvider;
import org.visallo.webster.App;
import org.visallo.webster.handlers.StaticResourceHandler;
import org.visallo.webster.socketio.transport.websockets.SocketIOServletContextAttributeListener;
import org.visallo.webster.socketio.transport.websockets.SocketIOServletContextAttributeListener;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

public class SocketIO {
    private static SocketIO instance;

    public static SocketIO getInstance() {
        return instance;
    }

    public static SocketIO init(App app, ServletContext servletContext, Map<String, String> config) {
        if (instance != null) {
            throw new WebsterSocketIOException("SocketIO instance already initialized");
        }
        instance = new SocketIO();

        app.get("/socket.io/socket.io.js", new StaticResourceHandler(SocketIO.class, "socket.io.js", "text/javascript"));
        app.get("/socket.io/socket.io.js.map", new StaticResourceHandler(SocketIO.class, "socket.io.js.map", "application/json"));
        app.get("/socket.io/socket.io.slim.js", new StaticResourceHandler(SocketIO.class, "socket.io.slim.js", "text/javascript"));
        app.get("/socket.io/socket.io.slim.js.map", new StaticResourceHandler(SocketIO.class, "socket.io.slim.js.map", "application/json"));
        app.get("/socket.io/", SocketIOHandler.class);
        app.post("/socket.io/", SocketIOHandler.class);

        registerWebSocketEndPoint(servletContext);

        ServletConfig servletConfig = new ServletConfig() {
            @Override
            public String getServletName() {
                return "socket.io";
            }

            @Override
            public ServletContext getServletContext() {
                return servletContext;
            }

            @Override
            public String getInitParameter(String name) {
                return config.get(name);
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return Collections.enumeration(config.keySet());
            }
        };
        try {
            ServletConfigHolder.getInstance().setConfig(servletConfig);
            TransportProvider transportProvider = new WebsocketTransportProvider();
            transportProvider.init(servletConfig, servletContext);
            SocketIOManager.getInstance().setTransportProvider(transportProvider);
        } catch (Exception ex) {
            throw new WebsterSocketIOException("could not config socket io library", ex);
        }

        return getInstance();
    }

    private static void registerWebSocketEndPoint(ServletContext servletContext) {
        ServerContainer serverContainer = (ServerContainer) servletContext.getAttribute(ServerContainer.class.getName());
        if (serverContainer != null) {
            try {
                serverContainer.addEndpoint(WebsocketTransportConnection.class);
            } catch (DeploymentException ex) {
                throw new WebsterSocketIOException("Could not add endpoint", ex);
            }
            return;
        }

        servletContext.addListener(SocketIOServletContextAttributeListener.class);
    }

    public Namespace getNamespace(String ns) {
        SocketIOManager manager = SocketIOManager.getInstance();
        Namespace namespace = manager.getNamespace(ns);
        if (namespace == null) {
            namespace = manager.createNamespace(ns);
        }
        return namespace;
    }
}
