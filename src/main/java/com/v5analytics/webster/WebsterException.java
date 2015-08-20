package com.v5analytics.webster;

public class WebsterException extends RuntimeException {
    public WebsterException(Throwable cause) {
        super(cause);
    }

    public WebsterException(String message) {
        super(message);
    }

    public WebsterException(String message, Throwable cause) {
        super(message, cause);
    }
}
