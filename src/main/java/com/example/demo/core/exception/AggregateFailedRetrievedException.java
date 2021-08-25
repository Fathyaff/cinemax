package com.example.demo.core.exception;

/**
 * @author fathyaff
 * @date 15/08/21 21.51
 */
public class AggregateFailedRetrievedException extends RuntimeException {
    public AggregateFailedRetrievedException(String message, Throwable e) {
        super(message, e);
    }
}
