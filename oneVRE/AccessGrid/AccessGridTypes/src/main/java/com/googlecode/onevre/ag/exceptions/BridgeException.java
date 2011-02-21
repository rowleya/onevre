package com.googlecode.onevre.ag.exceptions;

import java.io.IOException;

public class BridgeException extends IOException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new BridgeException
     * @param message The message
     */
    public BridgeException(String message) {
        super(message);
    }

    /**
     * Creates a new BridgeException
     * @param e The exception that caused this one
     */
    public BridgeException(Exception e) {
        super(e);
    }

}
