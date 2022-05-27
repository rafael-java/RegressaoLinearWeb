package com.example.demo_proc.models;

import java.io.Serializable;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class DadoModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private Double xInicial;
	private Double yInicial;

	private Double yPredito;
	private Double erro;
	private Double erroVX;
	private Double erroEQdrado;

	public DadoModel() {
		this.resetDados();
	}

	public void resetDados() {
		this.setErro(0D);
		this.setErroEQdrado(0D);
		this.setErroVX(0D);
		this.setYPredito(0D);
	}
}
