package com.example.demo_proc.models.principal;

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
	// Soma dos erros ao quadrado
	private Double somaErroEQdrado;
	private Double erroMedio;
	private Double coefA;
	private Double coefB;
	private Double coefANovo;
	private Double coefBNovo;
	private Integer indice;

	public ComparavelModel(Double coefA, Double coefB, Integer indice) {
		setCoefA(coefA);
		setCoefB(coefB);
		setIndice(indice);
	}
}
