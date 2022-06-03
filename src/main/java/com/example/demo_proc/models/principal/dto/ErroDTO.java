package com.example.demo_proc.models.principal.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErroDTO {
	
	private Integer indice;
	private Double erroMedio;
	private Boolean subindo;
	private Boolean menorErro;
	
	public ErroDTO() {
		this.menorErro = false;
	}
}
