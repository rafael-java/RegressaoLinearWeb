package com.example.demo_proc.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo_proc.error_exception.ResourceBadRequestException;
import com.example.demo_proc.models.escala.EscalaModel;
import com.example.demo_proc.models.escala.MinMaxDTO;
import com.example.demo_proc.models.principal.Coisas;
import com.example.demo_proc.models.principal.DadoModel;
import com.example.demo_proc.models.principal.dto.CoefsLinhaDTO;
import com.example.demo_proc.models.principal.dto.ErrosDTO;
import com.example.demo_proc.models.web.RequestModel;

@Service
@Transactional
public class EscalaService {
	
	// x = dado informado pelo usuário
	// y = x * coeA + coeB
	
	protected EscalaModel gerarEscalaGraficoPrincipal(RequestModel re) {
		
		List<DadoModel> amostra = re.getAmostra();
		
		Double xMin = amostra.get(0).getXInicial();
		Double xMax = amostra.get(amostra.size()-1).getXInicial();
		
		Double yMin = amostra.get(0).getYInicial();
		Double yMax = amostra.get(amostra.size()-1).getYInicial();
		
		EscalaModel escal = new EscalaModel();
		
		if (ehDeGrandezaAlta(amostra.size(), amostra.get(0).getXInicial(), amostra.get(1).getXInicial())) {
			escal.setXMax(xMax + 2.0);
			escal.setXMin(xMin - 2.0);
		} else {
			escal.setXMax(xMax + 0.5);
			escal.setXMin(xMin - 0.5);
		}
		
		if (ehDeGrandezaAlta(amostra.size(), amostra.get(0).getYInicial(), amostra.get(1).getYInicial())) {
			escal.setYMax(yMax + 2.0);
			escal.setYMin(yMin - 2.0);
		} else {
			escal.setYMax(yMax + 0.5);
			escal.setYMin(yMin - 0.5);
		}
		
		escal.setYMax(escal.getYMax() * 100);
		escal.setYMin((escal.getYMin() / 1500) * -1);
		
		return escal;
	}

	
	protected EscalaModel gerarEscalaGraficoPrincipalPredito(RequestModel re, Double xMax, Double yMax) {

		EscalaModel escal = new EscalaModel();
		escal = gerarEscalaGraficoPrincipal(re);
		
		List<DadoModel> amostra = re.getAmostra();
		
		if (ehDeGrandezaAlta(amostra.size(), amostra.get(0).getXInicial(), amostra.get(1).getXInicial())) {
			escal.setXMax(xMax + 2.0);
		} else {
			escal.setXMax(xMax + 0.5);
		}
		
		if (ehDeGrandezaAlta(amostra.size(), amostra.get(0).getYInicial(), amostra.get(1).getYInicial())) {
			escal.setYMax(yMax + 2.0);
		} else {
			escal.setYMax(yMax + 0.5);
		}
	
		escal.setYMax(escal.getYMax() * 100);
		
		return escal;
	}


	public EscalaModel gerarEscalaGraficoErros(List<ErrosDTO> erros) {
		
		Double xMax = erros.get(erros.size()-1).getIndice().doubleValue();
		
		MinMaxDTO minMax = acharMinimosEMaximos(erros);
		Double yMin = minMax.getMin();
		Double yMax = minMax.getMax();
		
		EscalaModel escal = new EscalaModel();
		
		escal.setXMax(xMax + 1.0);
		escal.setXMin(0.0);
		
		if (ehDeGrandezaAlta(erros.size(), erros.get(0).getErroMedio(), erros.get(erros.size()-1).getErroMedio())) {
			escal.setYMax(yMax + 2.0);
			escal.setYMin(yMin - 2.0);
		} else {
			escal.setYMax(yMax + 0.5);
			escal.setYMin(yMin - 0.5);
		}
		
		escal.setYMax(escal.getYMax() - 50000);
		escal.setYMin((escal.getYMin()/1000)*-1);
				
		return escal;		
	
	}
	

	private MinMaxDTO acharMinimosEMaximos(List<? extends Coisas> erros) {
		
		Double erroMin = Double.MAX_VALUE;
		Double erroMax = -100.0;
		
		for (Object obj : erros) {
			if (obj instanceof ErrosDTO) {
				if (((ErrosDTO) obj).getErroMedio() > erroMax) {
					erroMax = ((ErrosDTO) obj).getErroMedio();
				} 
				
				if (((ErrosDTO) obj).getErroMedio() < erroMin) {
					erroMin = ((ErrosDTO) obj).getErroMedio();
				}
			}

			
		}
		
		return new MinMaxDTO(erroMin, erroMax);
	}
	
	public List<Double> calcularValores(List<DadoModel> amostra, List<CoefsLinhaDTO> coefs) {
		
		List<Double> retorna = new ArrayList<Double>();
		
		for (CoefsLinhaDTO coef : coefs) {
			Double coefA = coef.getCoefA();
			Double coefB = coef.getCoefB();
			for (DadoModel dado : amostra) {
				Double y = coefA * dado.getXInicial() + coefB;
				retorna.add(y);
			}
		}
		
		return retorna;
	}

	private Boolean ehDeGrandezaAlta(Integer tamanho, Double dado1, Double dado2) {

		if (tamanho < 2) {
			throw new ResourceBadRequestException("Amostra precisa ter mais que dois dados");
		} else if (dado1 > 1.5) {
			if (dado2 > 1.5) {
				return true;
			}
		}
		return false;
	}
}
