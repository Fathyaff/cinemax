package com.example.demo.core.exception;

/**
 * @author fathyaff
 * @date 15/08/21 21.49
 */
public class ProduceEventException extends RuntimeException {
    public ProduceEventException(String message, Throwable e) {
        super(message, e);
    }
}
