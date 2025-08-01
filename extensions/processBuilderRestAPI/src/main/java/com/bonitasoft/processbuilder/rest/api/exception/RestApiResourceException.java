package com.bonitasoft.processbuilder.rest.api.exception;

/**
 * Custom exception to be thrown when a REST API resource cannot be processed.
 * For example, if a file cannot be read from the classpath.
 */
public class RestApiResourceException extends RuntimeException {

    public RestApiResourceException(String message) {
        super(message);
    }

    public RestApiResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}