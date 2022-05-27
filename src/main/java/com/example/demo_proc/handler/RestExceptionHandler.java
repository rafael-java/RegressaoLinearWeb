package com.example.demo_proc.handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo_proc.error_exception.ErrorObject;
import com.example.demo_proc.error_exception.InternalServerErrorException;
import com.example.demo_proc.error_exception.MessageErrorCustom;
import com.example.demo_proc.error_exception.ResourceBadRequestException;
import com.example.demo_proc.error_exception.ResourceForbiddenException;
import com.example.demo_proc.error_exception.ResourceNotFoundException;
import com.example.demo_proc.error_exception.ResourceUnauthorizedException;

@RestControllerAdvice
public class RestExceptionHandler {

	private List<ErrorObject> getErrors(final MethodArgumentNotValidException ex) {

		final List<ErrorObject> erros = new ArrayList<ErrorObject>();

		ex.getBindingResult().getFieldErrors().stream().forEach(error -> {
			final ErrorObject er = new ErrorObject();
			er.setMessage(error.getDefaultMessage());
			er.setField(error.getField());
			er.setParameter(error.getRejectedValue());
			erros.add(er);
		});

		return erros;
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<?> handlerResourceNotFoundException(final ResourceNotFoundException exception,
			final ServletRequest request) {
		final String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss"));

		final MessageErrorCustom custom = new MessageErrorCustom();
		custom.setDatahora(data);
		custom.setStatus(HttpStatus.NOT_FOUND.value());
		custom.setError("NOT FOUND");
		custom.setMensagem(exception.getMessage());
		custom.setPath(((HttpServletRequest) request).getRequestURI());

		return new ResponseEntity<>(custom, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ResourceForbiddenException.class)
	public ResponseEntity<?> handlerResourceForbiddenException(final ResourceForbiddenException exception,
			final ServletRequest request) {
		final String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss"));

		final MessageErrorCustom custom = new MessageErrorCustom();
		custom.setDatahora(data);
		custom.setStatus(HttpStatus.FORBIDDEN.value());
		custom.setError("Forbidden");
		custom.setMensagem(exception.getMessage());
		custom.setPath(((HttpServletRequest) request).getRequestURI());

		return new ResponseEntity<>(custom, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(ResourceUnauthorizedException.class)
	public ResponseEntity<?> handlerResourceUnauthorizedException(final ResourceUnauthorizedException exception,
			final ServletRequest request) {
		final String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss"));
		final MessageErrorCustom custom = new MessageErrorCustom();
		custom.setDatahora(data);
		custom.setStatus(HttpStatus.UNAUTHORIZED.value());
		custom.setError("Unauthorized");
		custom.setMensagem(exception.getMessage());
		custom.setPath(((HttpServletRequest) request).getRequestURI());

		return new ResponseEntity<>(custom, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(ResourceBadRequestException.class)
	public ResponseEntity<?> handlerResourceBadRequestException(final ResourceBadRequestException exception,
			final ServletRequest request) {
		final String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss"));

		final MessageErrorCustom custom = new MessageErrorCustom();
		custom.setDatahora(data);
		custom.setStatus(HttpStatus.BAD_REQUEST.value());
		custom.setError("Bad Request");
		custom.setMensagem(exception.getMessage());
		custom.setPath(((HttpServletRequest) request).getRequestURI());

		return new ResponseEntity<>(custom, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handlerMethodArgumentNotValidException(final MethodArgumentNotValidException exception,
			final ServletRequest request) {

		final List<ErrorObject> errors = getErrors(exception);

		final String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss"));

		final MessageErrorCustom custom = new MessageErrorCustom();
		custom.setDatahora(data);
		custom.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
		custom.setError("Method Not Allowed");
		custom.setMensagem("Requisição possui campos inválidos");
		custom.setPath(((HttpServletRequest) request).getRequestURI());
		custom.setErrors(errors);

		return new ResponseEntity<>(custom, HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(InternalServerErrorException.class)
	public ResponseEntity<?> handlerInternalServerError(final InternalServerErrorException exception,
			final ServletRequest request) {

		final String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss"));

		final MessageErrorCustom custom = new MessageErrorCustom();
		custom.setDatahora(data);
		custom.setMensagem(exception.getMessage());
		custom.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		custom.setError("Internal Server Error");
		custom.setPath(((HttpServletRequest) request).getRequestURI());

		return new ResponseEntity<>(custom, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
