package com.example.demo_proc.error_exception;

import lombok.Data;

@Data
public class ErrorObject {

	private String message;
	private String field;
	private Object parameter;
}