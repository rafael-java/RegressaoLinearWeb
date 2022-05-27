package com.example.demo_proc.models;

import java.io.Serializable;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ComparavelModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private Double somaErro;
	private Double somaErroVX;
	private Double somaErroEQdrado;
	private Double erroMedio;
	private Double coefA;
	private Double coefB;
	private Double coefANovo;
	private Double coefBNovo;

	public ComparavelModel(Double coefA, Double coefB) {
		setCoefA(coefA);
		setCoefB(coefB);
	}
}
