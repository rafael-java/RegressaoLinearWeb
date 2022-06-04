
package com.example.demo_proc.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo_proc.error_exception.ResourceBadRequestException;
import com.example.demo_proc.models.escala.EscalaModel;
import com.example.demo_proc.models.escala.MinMaxDTO;
import com.example.demo_proc.models.principal.DadoModel;
import com.example.demo_proc.models.principal.dto.CoefsLinhaDTO;
import com.example.demo_proc.models.principal.dto.ErroDTO;
import com.example.demo_proc.models.web.RequestModel;

@Service
@Transactional
public class EscalaService {

	// x = dado informado pelo usuário
	// y = x * coeA + coeB

	protected EscalaModel gerarEscalaGraficoPrincipal(RequestModel req, List<CoefsLinhaDTO> coefs) {

		List<DadoModel> amostra = req.getAmostra();
		
		Double xMin = amostra.get(0).getXInicial();
		Double xMax = amostra.get(amostra.size() - 1).getXInicial();

		MinMaxDTO minMax = acharMinimosEMaximos_amostra(amostra, coefs);
		Double yMin = minMax.getMin();
		Double yMax = minMax.getMax();

		EscalaModel escal = new EscalaModel();

		// ver isso
		if (ehMaiorQueDez(amostra.size(), xMax)) {
			Double percentMax = xMax * 0.05;
			Double percentMin = xMin * 0.05;
			escal.setXMax(xMax + percentMax);
			escal.setXMin(xMin - percentMin);
		} else {
			escal.setXMax(xMax + 0.5);
			escal.setXMin(xMin - 0.5);
		}

		// ver isso
		if (ehMaiorQueDez(amostra.size(), yMax)) {
			Double percentMax = yMax * 0.1;
			if(percentMax < 0) {
				percentMax = percentMax * -1;
			}
//			Double percentMin = yMin * 0.1;
			escal.setYMax(yMax + percentMax);
//			escal.setYMin(yMin - percentMin);
		} else {
			escal.setYMax(yMax + 0.5);
//			escal.setYMin(yMin - 0.5);
		}
		
		if (ehMaiorQueDez(amostra.size(), yMin)) {
//			Double percentMax = yMax * 0.05;
			Double percentMin = yMin * 0.1;
			if(percentMin < 0) {
				percentMin = percentMin * -1;
			}
//			escal.setYMax(yMax + percentMax);
			escal.setYMin(yMin - percentMin);
		} else {
//			escal.setYMax(yMax + 0.5);
			escal.setYMin(yMin - 0.5);
		}

//		escal.setYMax(escal.getYMax() * 100);
//		escal.setYMin((escal.getYMin() / 1500) * -1);

		return escal;
	}

	protected EscalaModel gerarEscalaGraficoPrincipal_Predito(RequestModel req, Double xMax, Double yMax, List<CoefsLinhaDTO> coefs) {

		EscalaModel escal = new EscalaModel();
		escal = gerarEscalaGraficoPrincipal(req, coefs);

		List<DadoModel> amostra = req.getAmostra();

		if (ehMaiorQueDez(amostra.size(), xMax)) {
			Double percentMax = xMax * 0.05;
			if(percentMax < 0) {
				percentMax = percentMax * -1;
			}
			escal.setXMax(xMax + percentMax);
		} else {
			escal.setXMax(xMax + 0.5);
		}

		if (ehMaiorQueDez(amostra.size(), yMax)) {
			Double percentMax = yMax * 0.1;
			if(percentMax < 0) {
				percentMax = percentMax * -1;
			}
			escal.setYMax(yMax + percentMax);
		} else {
			escal.setYMax(yMax + 5);
		}

//		escal.setYMax(escal.getYMax() * 100);

		return escal;
	}

	public EscalaModel gerarEscalaGraficoErros(List<ErroDTO> erros) {

		Double xMax = erros.get(erros.size() - 1).getIndice().doubleValue();

		MinMaxDTO minMax = acharMinimosEMaximos_erros(erros);
		Double yMin = minMax.getMin();
		Double yMax = minMax.getMax();

		EscalaModel escal = new EscalaModel();

		escal.setXMax(xMax + 1.0);
		escal.setXMin(0.0);

		if (ehMaiorQueDez(erros.size(), yMax)) {
			Double percentMax = yMax * 0.1;
			if(percentMax < 0) {
				percentMax = percentMax * -1;
			}
//			Double percentMin = yMin * 0.05;
			escal.setYMax(yMax + percentMax);
//			escal.setYMin(yMin - percentMin);
		} else {
			escal.setYMax(yMax + 0.5);
//			escal.setYMin(yMin - 0.5);
		}

		if (ehMaiorQueDez(erros.size(), yMin)) {
//			Double percentMax = yMax * 0.05;
			Double percentMin = yMin * 0.1;
			if(percentMin < 0) {
				percentMin = percentMin * -1;
			}
//			escal.setYMax(yMax + percentMax);
			escal.setYMin(yMin - percentMin);
		} else {
//			escal.setYMax(yMax + 0.5);
			escal.setYMin(yMin - 0.5);
		}
		
//		escal.setYMax(escal.getYMax() - 50000);
//		escal.setYMin((escal.getYMin() / 1000) * -1);

		return escal;

	}
	

	private MinMaxDTO acharMinimosEMaximos_amostra(List<DadoModel> amostra, List<CoefsLinhaDTO> coefs) {
		
		List<Double> valores = calcularValores(amostra, coefs);
		
		MinMaxDTO minMax = new MinMaxDTO();

		acharMinimosEMaximos(minMax, valores);
		
		return minMax;
	}

	private MinMaxDTO acharMinimosEMaximos_erros(List<ErroDTO> erros) {

		List<Double> valores = new ArrayList<Double>();
		
		for (ErroDTO erro : erros) {
			valores.add(erro.getErroMedio());
		}
		
		MinMaxDTO minMax = new MinMaxDTO();

		acharMinimosEMaximos(minMax, valores);
		
		return minMax;
	}

	private void acharMinimosEMaximos(MinMaxDTO minMax, List<Double> valores) {
		for (Double valor : valores) {
			if (valor > minMax.getMax()) {
				minMax.setMax(valor);
			}

			if (valor < minMax.getMin()) {
				minMax.setMin(valor); 
			}
		}
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
	
	private Boolean ehMaiorQueDez(Integer tamanho, Double dado1) {

		if (tamanho < 2) {
			throw new ResourceBadRequestException("Verifique os valores informados. Obs: Amostra precisa de mais de dois dados ");
		} else if (dado1 < 0) {
			dado1 = dado1 * -1;
		}
			
		if (dado1 > 10.0) {
			return true;
		} 
		
		return false;
	}
}
