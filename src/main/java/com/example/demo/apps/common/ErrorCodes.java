package com.example.demo.apps.common;

import org.springframework.http.HttpStatus;

public enum ErrorCodes {

	// @formatter:off
	// Bad request
	BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "40000", "Bad request", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST.value(), "40001", "Invalid request", HttpStatus.BAD_REQUEST),

	// Not found
    DATA_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "40404", "Data not found", HttpStatus.NOT_FOUND),

	// Internal server error
	GENERIC_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "50001", "Server error", HttpStatus.INTERNAL_SERVER_ERROR);
    // @formatter:on

	private final int statusCode;

	private final String errorCode;

	private final String message;

	private final HttpStatus httpStatus;

	ErrorCodes(int statusCode, String errorCode, String message, HttpStatus httpStatus) {
		this.statusCode = statusCode;
		this.errorCode = errorCode;
		this.message = message;
		this.httpStatus = httpStatus;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getMessage() {
		return message;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public int getHttpStatusCode() {
		return httpStatus.value();
	}

	public static ErrorCodes fromString(int statusCode) {

		for (ErrorCodes rs : ErrorCodes.values()) {
			if (rs.getStatusCode() == statusCode) {
				return rs;
			}
		}

		return null;
	}

}
