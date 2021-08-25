package com.example.demo.core.exception;

/**
 * @author fathyaff
 * @date 15/08/21 23.24
 */
public class SaveEventStoreException extends RuntimeException{
    public SaveEventStoreException(String message) {
        super(message);
    }

    public SaveEventStoreException(String message, Throwable e) {
        super(message, e);
    }
}
