package com.example.demo_proc.models.escala;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MinMaxDTO {

	private Double min;
	private Double max;
	
	public MinMaxDTO () {
		this.min = Double.MAX_VALUE;
		this.max = Double.MIN_VALUE;
	}
}
