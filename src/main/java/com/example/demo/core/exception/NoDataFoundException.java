package com.example.demo.core.exception;

/**
 * @author fathyaff
 * @date 15/08/21 01.14
 */
public class NoDataFoundException extends RuntimeException {
    public NoDataFoundException(String message) {
        super(message);
    }
}
