package com.example.demo_proc.models.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import com.example.demo_proc.models.principal.DadoModel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RequestModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<DadoModel> amostra = new ArrayList<DadoModel>();
	private Double taxaDeAprendizagem;
	private Double coefAInicial;
	private Double coefBInicial;
	private int iteracaoMax;
	private Double valorXParaPredizer;
}
