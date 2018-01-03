package org.visallo.webster.socketio.chat;

import com.codeminders.socketio.common.SocketIOException;
import com.codeminders.socketio.protocol.SocketIOProtocol;
import org.visallo.webster.App;
import org.visallo.webster.WebsterException;
import org.visallo.webster.handlers.StaticResourceHandler;
import org.visallo.webster.socketio.SocketIO;
import org.visallo.webster.socketio.WebsterSocketIOException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.ServerContainer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChatApp {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new WebsterException("Usage <port>");
        }
        int port = Integer.parseInt(args[0]);

        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new TestServlet()), "/*");

        ServerContainer serverContainer = WebSocketServerContainerInitializer.configureContext(context);
        context.setAttribute(ServerContainer.class.getName(), serverContainer);

        server.start();
        System.out.println("Listening http://localhost:" + port);
        server.join();
    }

    private static class TestServlet extends HttpServlet {
        private App app;
        private SocketIO io;
        private int numUsers;

        @Override
        public void init(ServletConfig servletConfig) throws ServletException {
            super.init(servletConfig);

            app = new App(this.getServletContext());
            Map<String, String> socketioConfig = new HashMap<>();
            io = SocketIO.init(app, this.getServletContext(), socketioConfig);

            app.get("/main.js", new StaticResourceHandler(ChatApp.class, "main.js", "text/javascript"));
            app.get("/style.css", new StaticResourceHandler(ChatApp.class, "style.css", "text/css"));
            app.get("/", new StaticResourceHandler(ChatApp.class, "index.html", "text/html"));

            io.getNamespace(SocketIOProtocol.DEFAULT_NAMESPACE).on(socket -> {
                final SocketData socketData = new SocketData();
                socket.join("room");

                socket.on("new message", (name, args, ackRequested) -> {
                    NewMessageMessage newMessageMessage = new NewMessageMessage();
                    newMessageMessage.username = socketData.username;
                    newMessageMessage.message = (String) args[0];
                    try {
                        socket.broadcast("room", "new message", newMessageMessage);
                    } catch (SocketIOException ex) {
                        throw new WebsterSocketIOException("Could not emit", ex);
                    }
                    return null;
                });

                socket.on("add user", (name, args, ackRequested) -> {
                    if (socketData.addedUser) {
                        return null;
                    }

                    socketData.username = (String) args[0];
                    numUsers++;
                    socketData.addedUser = true;

                    LoginMessage loginMessage = new LoginMessage();
                    loginMessage.numUsers = numUsers;
                    try {
                        socket.emit("login", loginMessage);
                    } catch (SocketIOException ex) {
                        throw new WebsterSocketIOException("Could not emit", ex);
                    }

                    // echo globally (all clients) that a person has connected
                    UserJoinedMessage userJoinedMessage = new UserJoinedMessage();
                    userJoinedMessage.username = socketData.username;
                    userJoinedMessage.numUsers = numUsers;
                    try {
                        socket.broadcast("room", "user joined", userJoinedMessage);
                    } catch (SocketIOException ex) {
                        throw new WebsterSocketIOException("Could not emit", ex);
                    }
                    return null;
                });

                // when the client emits 'typing', we broadcast it to others
                socket.on("typing", (name, args, ackRequested) -> {
                    TypingMessage typingMessage = new TypingMessage();
                    typingMessage.username = socketData.username;
                    try {
                        socket.broadcast("room", "typing", typingMessage);
                    } catch (SocketIOException ex) {
                        throw new WebsterSocketIOException("Could not emit", ex);
                    }
                    return null;
                });

                // when the client emits 'stop typing', we broadcast it to others
                socket.on("stop typing", (name, args, ackRequested) -> {
                    TypingMessage typingMessage = new TypingMessage();
                    typingMessage.username = socketData.username;
                    try {
                        socket.broadcast("room", "stop typing", typingMessage);
                    } catch (SocketIOException ex) {
                        throw new WebsterSocketIOException("Could not emit", ex);
                    }
                    return null;
                });

                // when the user disconnects.. perform this
                socket.on((disconnectingSocket, reason, errorMessage) -> {
                    if (!socketData.addedUser) {
                        return;
                    }
                    numUsers--;

                    // echo globally that this client has left
                    UserLeftMessage userLeftMessage = new UserLeftMessage();
                    userLeftMessage.username = socketData.username;
                    userLeftMessage.numUsers = numUsers;
                    try {
                        disconnectingSocket.broadcast("room", "user left", userLeftMessage);
                    } catch (SocketIOException ex) {
                        throw new WebsterSocketIOException("Could not emit", ex);
                    }
                });
            });
        }

        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            try {
                app.handle(req, resp);
            } catch (WebsterException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new WebsterException("Could not service request", ex);
            }
        }

        private static class SocketData {
            boolean addedUser;
            String username;
        }

        private static class TypingMessage {
            public String username;
        }

        private static class NewMessageMessage {
            public String username;
            public String message;
        }

        private static class LoginMessage {
            public int numUsers;
        }

        private static class UserJoinedMessage {
            public String username;
            public int numUsers;
        }

        private static class UserLeftMessage {
            public String username;
            public int numUsers;
        }
    }
}
