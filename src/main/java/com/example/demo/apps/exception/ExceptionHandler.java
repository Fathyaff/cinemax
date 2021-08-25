package com.example.demo.apps.exception;

import com.example.demo.apps.common.ErrorCodes;
import com.example.demo.core.exception.InvalidRequestException;
import com.example.demo.core.exception.NoDataFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler extends BaseExceptionHandler {

	public ExceptionHandler() {
		registerMapping(InvalidRequestException.class, ErrorCodes.INVALID_REQUEST);
		registerMapping(NoDataFoundException.class, ErrorCodes.DATA_NOT_FOUND);
	}

}
