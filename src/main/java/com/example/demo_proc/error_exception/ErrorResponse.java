package com.example.demo_proc.error_exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
public @Data class ErrorResponse {

	private final String message;
	private final int code;
	private final String status;
	private final String objectName;
	private final List<ErrorObject> errors;
}