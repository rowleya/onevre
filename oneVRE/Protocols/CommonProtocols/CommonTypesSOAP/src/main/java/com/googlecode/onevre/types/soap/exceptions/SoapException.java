package com.googlecode.onevre.types.soap.exceptions;

/**
 * An exception thrown when an Invalid SoapMessage is presented
 * @author Tobias M Schiebeck
 * @version 1.0
 */

public class SoapException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new SoapException
     * @param message The message
     */
    public SoapException(String message) {
        super(message);
    }

    /**
     * Creates a new SoapException
     * @param e The exception that caused this one
     */
    public SoapException(Exception e) {
        super(e);
    }

}
