package com.example.demo.core.exception;

/**
 * @author fathyaff
 * @date 15/08/21 00.17
 */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
