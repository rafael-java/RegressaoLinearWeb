package com.example.demo_proc.error_exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class MessageErrorCustom {

	private String datahora;
	private Integer status;
	private String error;
	private String mensagem;
	private String path;
	private List<ErrorObject> errors;

	public MessageErrorCustom(String datahora, Integer status, String error, String mensagem, String path) {
		super();
		this.datahora = datahora;
		this.status = status;
		this.error = error;
		this.mensagem = mensagem;
		this.path = path;
	}

	public MessageErrorCustom() {
	}

	public String getDatahora() {
		return datahora;
	}

	public void setDatahora(String datahora) {
		this.datahora = datahora;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<ErrorObject> getErrors() {
		return errors;
	}

	public void setErrors(List<ErrorObject> errors) {
		this.errors = errors;
	}

}