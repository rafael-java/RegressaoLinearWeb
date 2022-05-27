package com.example.demo_proc.models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ResponseModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<ErrosDTO> erros;
	private ComparavelModel estruturaCorreta;
	private Double yPredicao;
	private EscalaModel escalaDoGraficoErros;
	private EscalaModel escalaDoGraficoPrincipal;

}
