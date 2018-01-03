package org.visallo.webster.socketio;

import org.visallo.webster.WebsterException;

public class WebsterSocketIOException extends WebsterException {
    public WebsterSocketIOException(String message) {
        super(message);
    }

    public WebsterSocketIOException(String message, Throwable cause) {
        super(message, cause);
    }
}
