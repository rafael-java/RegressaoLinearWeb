package com.example.demo_proc.models.principal.dto;

import com.example.demo_proc.models.principal.Coisas;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrosDTO implements Coisas {
	
	private Integer indice;
	private Double erroMedio;
	private Boolean subindo;
	
}
