package com.example.demo.core.exception;

/**
 * @author fathyaff
 * @date 15/08/21 23.24
 */
public class DeactivateEventStoreException extends RuntimeException{
    public DeactivateEventStoreException(String message, Throwable e) {
        super(message, e);
    }
}
