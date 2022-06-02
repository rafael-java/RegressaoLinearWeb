package com.example.demo_proc.models.web;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;

import com.example.demo_proc.models.escala.EscalaModel;
import com.example.demo_proc.models.principal.ComparavelModel;
import com.example.demo_proc.models.principal.dto.CoefsLinhaDTO;
import com.example.demo_proc.models.principal.dto.ErrosDTO;

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
	private List<CoefsLinhaDTO> coefsLinhas;
	private ComparavelModel estruturaCorreta;
	private Double yPredicao;
	
	private EscalaModel escalaDoGraficoErros;
	private EscalaModel escalaDoGraficoPrincipal;

}
