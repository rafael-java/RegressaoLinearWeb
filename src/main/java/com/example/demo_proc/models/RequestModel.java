package com.example.demo_proc.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

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
	// 0.000155 - julio
	// 0.00415
	private Double coefAInicial;
	// 0.225;
	// 0.4 - julio
	private Double coefBInicial;
	// -440.0;
	// -388.0 - julio
	private int iteracaoMax;
	// 10 - julio
	private Double valorXParaPredizer;
}
