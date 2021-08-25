package com.example.demo.apps.response;

import com.example.demo.apps.common.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlRootElement(name = "response")
@XmlType(propOrder = { "status", "statusCode" })
@JsonPropertyOrder({ "statusCode", "status" })
public abstract class Response implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String status;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private int statusCode;

	@JsonIgnore()
	private HttpStatus httpStatus;

	public Response(String status, int statusCode, HttpStatus httpStatus) {
		super();
		this.status = status;
		this.statusCode = statusCode;
		this.httpStatus = httpStatus;
	}

	public Response() {
		setHttpStatus(HttpStatus.OK);
	}

	public Response(String status, int statusCode) {
		this.status = status;
		this.statusCode = statusCode;
	}

	@XmlElement(name = "status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String type) {
		this.status = type;
	}

	@XmlElement(name = "statusCode")
	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	@XmlElement(name = "httpStatus")
	@JsonIgnore()
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	public void setResponse(ErrorCodes responseStatus) {
		this.httpStatus = responseStatus.getHttpStatus();
		this.statusCode = responseStatus.getStatusCode();
		this.status = responseStatus.getHttpStatus().name();
	}

}
