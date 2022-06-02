package com.example.demo_proc.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo_proc.error_exception.ResourceBadRequestException;
import com.example.demo_proc.models.principal.ComparavelModel;
import com.example.demo_proc.models.principal.DadoModel;
import com.example.demo_proc.models.principal.dto.CoefsLinhaDTO;
import com.example.demo_proc.models.principal.dto.ErrosDTO;
import com.example.demo_proc.models.web.RequestModel;
import com.example.demo_proc.models.web.ResponseModel;

@Service
@Transactional
public class RegressaoLinearService {

	@Autowired
	EscalaService escalaService;
	
	private static ComparavelModel anterior = new ComparavelModel();

	public ResponseModel acharModelo(RequestModel re) {
		int iteracao = 0;
		Boolean subindo = false;

		ResponseModel res = new ResponseModel();

		List<CoefsLinhaDTO> coefsLinhas = new ArrayList<CoefsLinhaDTO>();
		List<ErrosDTO> erros = new ArrayList<ErrosDTO>();

		ComparavelModel atual = new ComparavelModel(re.getCoefAInicial(), re.getCoefBInicial(), iteracao+1);

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

			if (!subindo) {
				if (iteracao > 2 && atual.getErroMedio() >= anterior.getErroMedio()) {
					// Achou o erro mínimo
					// Assumindo que não vai ficar subindo e descendo
					// A iteração é 2 pois na primeira iteração não dá pra saber se tá subindo (ex: 3 -> 2 -> 3)
					// Se ele fizer (3 -> 2 -> 3, indica o erro abaixo)
					// Se ele fizer (3 -> 2 -> 1 -> 3), ok

					res.setEstruturaCorreta(anterior);
					subindo = true;
					
				} else if (iteracao > 0 && atual.getErroMedio() > anterior.getErroMedio()) {
					// Subiu na segunda iteração, está errado
					// Pois a taxa de aprendizagem está muito grande
					// tem que ser maior que 0, pois na primeira iteração não há anterior
					
					throw new ResourceBadRequestException("ERRO! A taxa de aprendizagem está muito alta");
				}
			}
			
//			// sofisticar: resubmete com uma taxa menor, ai da uma taxa boa

			ErrosDTO erroDTO = new ErrosDTO();
			erroDTO.setSubindo(subindo);

			CoefsLinhaDTO coefsLinha = new CoefsLinhaDTO();

			if (iteracao == re.getIteracaoMax()) {
				// IteracaoMax = quando para
//				// Iteracao maxima = limite, como se fosse um delta
				// Não achou o erro mínimo, mas chegou na iteração máxima
				
				erroDTO.setErroMedio(atual.getErroMedio());
				erroDTO.setIndice(iteracao + 1);
				erros.add(erroDTO);

				coefsLinha.setCoefA(anterior.getCoefANovo());
				coefsLinha.setCoefB(anterior.getCoefBNovo());
				coefsLinha.setIndice(iteracao + 1);
				coefsLinhas.add(coefsLinha);

				res.setErros(erros);
				res.setCoefsLinhas(coefsLinhas);

				res.setEscalaDoGraficoErros(escalaService.gerarEscalaGraficoErros(erros));
				res.setEscalaDoGraficoPrincipal(escalaService.gerarEscalaGraficoPrincipal(re));

				return res;
			} else {
				// Está achando o erro mínimo, ainda.
				iteracao++;
				anterior = atual;
				erroDTO.setErroMedio(anterior.getErroMedio());
				erroDTO.setIndice(iteracao);
				erros.add(erroDTO);

				atual = new ComparavelModel(anterior.getCoefANovo(), anterior.getCoefBNovo(), iteracao+1);

				coefsLinha.setCoefA(anterior.getCoefA());
				coefsLinha.setCoefB(anterior.getCoefB());
				coefsLinha.setIndice(iteracao);
				coefsLinhas.add(coefsLinha);

				resetDadosAmostra(re.getAmostra());
			}
		}
	}

	public ResponseModel acharModeloEPredizer(RequestModel re) {
		ResponseModel res = acharModelo(re);
		ComparavelModel comp = res.getEstruturaCorreta();
		
		Double predicao = re.getValorXParaPredizer() * comp.getCoefA() + comp.getCoefB();
		res.setYPredicao(predicao);
		res.setEscalaDoGraficoPrincipal(escalaService.gerarEscalaGraficoPrincipalPredito(re, re.getValorXParaPredizer(), predicao));
		return res;
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
	
}
