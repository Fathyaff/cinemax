package com.example.demo.apps.exception;

import com.example.demo.apps.response.BasicError;
import com.example.demo.apps.common.ErrorCodes;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseExceptionHandler {

	private static final BasicError DEFAULT_ERROR = new BasicError(ErrorCodes.GENERIC_SERVER_ERROR);

	@SuppressWarnings("rawtypes")
	private final Map<Class, BasicError> errorMappings = new HashMap<>();

	public BaseExceptionHandler() {

	}

	@ExceptionHandler(Throwable.class)
	@ResponseBody
	public BasicError handleThrowable(final Throwable ex, final HttpServletResponse response) {
		BasicError mapping = errorMappings.getOrDefault(ex.getClass(), DEFAULT_ERROR);
		response.setStatus(mapping.getStatusCode());
		mapping.setDescription(ex.getMessage());
		return mapping;
	}

	protected void registerMapping(final Class<?> clazz, final ErrorCodes status) {
		errorMappings.put(clazz, new BasicError(status));
	}

}
