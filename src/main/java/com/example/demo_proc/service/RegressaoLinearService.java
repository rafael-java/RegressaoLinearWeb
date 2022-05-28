package com.example.demo_proc.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo_proc.error_exception.ResourceBadRequestException;
import com.example.demo_proc.models.CoefsLinhaDTO;
import com.example.demo_proc.models.ComparavelModel;
import com.example.demo_proc.models.DadoModel;
import com.example.demo_proc.models.ErrosDTO;
import com.example.demo_proc.models.EscalaModel;
import com.example.demo_proc.models.MinMaxDTO;
import com.example.demo_proc.models.RequestModel;
import com.example.demo_proc.models.ResponseModel;

@Service
@Transactional
public class RegressaoLinearService {

	private static ComparavelModel anterior = new ComparavelModel();

	public ResponseModel acharModelo(RequestModel re) {
		int iteracao = 0;
		Boolean subindo = false;

		ResponseModel res = new ResponseModel();

		List<CoefsLinhaDTO> coefsLinhas = new ArrayList<CoefsLinhaDTO>();
		List<ErrosDTO> erros = new ArrayList<ErrosDTO>();

		ComparavelModel atual = new ComparavelModel(re.getCoefAInicial(), re.getCoefBInicial());

		while (true) {
			for (DadoModel dado : re.getAmostra()) {
				dado.setYPredito(dado.getXInicial() * atual.getCoefA() + atual.getCoefB());
				dado.setErro(dado.getYPredito() - dado.getYInicial());
				dado.setErroVX(dado.getErro() * dado.getXInicial());
				dado.setErroEQdrado(dado.getErro() * dado.getErro());
			}

			somas(re.getAmostra(), atual);

			atual.setErroMedio(atual.getSomaErroEQdrado() / re.getAmostra().size());

			atual.setCoefANovo(atual.getCoefA()
					- re.getTaxaDeAprendizagem() * (2.0 / re.getAmostra().size()) * atual.getSomaErro());
			atual.setCoefBNovo(atual.getCoefB()
					- re.getTaxaDeAprendizagem() * (2.0 / re.getAmostra().size()) * atual.getSomaErroVX());

			if (iteracao > 2 && !subindo && atual.getErroMedio() >= anterior.getErroMedio()) {
				// Achou o erro mínimo
				// Assumindo que não vai ficar subindo e descendo

				res.setEstruturaCorreta(anterior);
				subindo = true;
			} else if (iteracao > 0 && !subindo && atual.getErroMedio() > anterior.getErroMedio()) {
				// Não achou o erro mínimo, pois a taxa de aprendizagem está muito grande
				throw new ResourceBadRequestException("A taxa de aprendizagem está muito alta");
			}
//			// sofisticar: resubmete com uma taxa menor, ai da uma taxa boa

			ErrosDTO erroDTO = new ErrosDTO();
			erroDTO.setSubindo(subindo);

			CoefsLinhaDTO coefsLinha = new CoefsLinhaDTO();

			if (iteracao == re.getIteracaoMax()) {
//				// ao invez de iteracao maxima, colocar um limite, como se fosse um delta
				// Não achou o erro mínimo, mas chegou na iteração máxima
				// IteracaoMax = quando para
				erroDTO.setErroMedio(atual.getErroMedio());
				erroDTO.setIndice(iteracao + 1);
				erros.add(erroDTO);

				coefsLinha.setCoefA(anterior.getCoefANovo());
				coefsLinha.setCoefB(anterior.getCoefBNovo());
				coefsLinha.setIndice(iteracao + 1);
				coefsLinhas.add(coefsLinha);

				res.setErros(erros);
				res.setCoefsLinhas(coefsLinhas);

				res.setEscalaDoGraficoErros(gerarEscalaGraficoErros(erros));
				res.setEscalaDoGraficoPrincipal(gerarEscalaGraficoPrincipal(re));

				return res;
			} else {
				// Está achando o erro mínimo, ainda.
				iteracao++;
				anterior = atual;
				erroDTO.setErroMedio(anterior.getErroMedio());
				erroDTO.setIndice(iteracao);
				erros.add(erroDTO);

				atual = new ComparavelModel(anterior.getCoefANovo(), anterior.getCoefBNovo());

				coefsLinha.setCoefA(anterior.getCoefA());
				coefsLinha.setCoefB(anterior.getCoefB());
				coefsLinha.setIndice(iteracao);
				coefsLinhas.add(coefsLinha);

				resetDadosAmostra(re.getAmostra());
			}
		}
	}

	private void somas(List<DadoModel> amostra, ComparavelModel atual) {

		Double totalErro = 0D;
		Double totalErroVX = 0D;
		Double totalErroEQdrado = 0D;

		for (DadoModel dado : amostra) {
			totalErro = totalErro + dado.getErro();
			totalErroVX = totalErroVX + dado.getErroVX();
			totalErroEQdrado = totalErroEQdrado + dado.getErroEQdrado();
		}

		atual.setSomaErro(totalErro);
		atual.setSomaErroVX(totalErroVX);
		atual.setSomaErroEQdrado(totalErroEQdrado);
	}

	private void resetDadosAmostra(List<DadoModel> amostra) {
		for (DadoModel dado : amostra) {
			dado.resetDados();
		}
	}

	public ResponseModel acharModeloEPredizer(RequestModel re) {
		ResponseModel res = acharModelo(re);
		ComparavelModel comp = acharModelo(re).getEstruturaCorreta();
		res.setEstruturaCorreta(comp);
		Double predicao = re.getValorXParaPredizer() * comp.getCoefA() + comp.getCoefB();
		res.setYPredicao(predicao);
		res.setEscalaDoGraficoPrincipal(gerarEscalaGraficoPrincipalPredito(re, re.getValorXParaPredizer(), predicao));
		return res;
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
		
		return escal;		
	}
	
	public MinMaxDTO acharMinimosEMaximos(List<ErrosDTO> erros) {
		
		Double erroMin = Double.MAX_VALUE;
		Double erroMax = -100.0;
		
		for (ErrosDTO erro : erros) {
			if (erro.getErroMedio() > erroMax) {
				erroMax = erro.getErroMedio();
			} 
			
			if (erro.getErroMedio() < erroMin) {
				erroMin = erro.getErroMedio();
			}
		}
		
		return new MinMaxDTO(erroMin, erroMax);
	}

	public EscalaModel gerarEscalaGraficoPrincipalPredito(RequestModel re, Double xMax, Double yMax) {

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
	
		return escal;
	}

	public EscalaModel gerarEscalaGraficoPrincipal(RequestModel re) {
		
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
		
		return escal;
	}

	public Boolean ehDeGrandezaAlta(Integer tamanho, Double dado1, Double dado2) {

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
