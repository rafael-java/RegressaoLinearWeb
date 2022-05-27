package com.example.demo_proc.error_exception;

import java.io.IOException;

public class FileStorageException extends Exception {

	private static final long serialVersionUID = 1L;

	public FileStorageException(String message, IOException e) {
		super(message);

	}
}
