package com.example.demo.apps.response;

import com.example.demo.apps.common.ErrorCodes;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicError extends Response {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String message;

	private String description;

	private String errorCode;

	public BasicError() {
		super();
		setResponse(ErrorCodes.BAD_REQUEST);
		this.message = ErrorCodes.BAD_REQUEST.getMessage();
		this.errorCode = ErrorCodes.BAD_REQUEST.getErrorCode();
	}

	public BasicError(ErrorCodes status) {
		super();
		setResponse(status);
		this.message = status.getMessage();
		this.errorCode = status.getErrorCode();
	}

}
