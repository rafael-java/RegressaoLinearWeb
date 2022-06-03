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
import com.example.demo_proc.models.principal.dto.ErroDTO;
import com.example.demo_proc.models.web.RequestModel;
import com.example.demo_proc.models.web.ResponseModel;

@Service
@Transactional
public class RegressaoLinearService {

	@Autowired
	EscalaService escalaService;

	private static ComparavelModel anterior = new ComparavelModel();

	public ResponseModel acharModelo(RequestModel req) {
		int iteracao = 0;
		Boolean subindo = false;

		ResponseModel res = new ResponseModel();

		List<CoefsLinhaDTO> coefsLinhas = new ArrayList<CoefsLinhaDTO>();
		List<ErroDTO> erros = new ArrayList<ErroDTO>();

		ComparavelModel atual = new ComparavelModel(req.getCoefAInicial(), req.getCoefBInicial(), iteracao + 1);

		while (true) {
			for (DadoModel dado : req.getAmostra()) {
				dado.setYPredito(dado.getXInicial() * atual.getCoefA() + atual.getCoefB());
				dado.setErro(dado.getYPredito() - dado.getYInicial());
				dado.setErroVX(dado.getErro() * dado.getXInicial());
				dado.setErroEQdrado(dado.getErro() * dado.getErro());
			}

			somas(req.getAmostra(), atual);

			atual.setErroMedio(atual.getSomaErroEQdrado() / req.getAmostra().size());

			atual.setCoefANovo(atual.getCoefA()
					- req.getTaxaDeAprendizagem() * (2.0 / req.getAmostra().size()) * atual.getSomaErro());
			atual.setCoefBNovo(atual.getCoefB()
					- req.getTaxaDeAprendizagem() * (2.0 / req.getAmostra().size()) * atual.getSomaErroVX());

			if (!subindo) {
				if (iteracao > 2 && atual.getErroMedio() >= anterior.getErroMedio()) {
					// Achou o erro mínimo
					// Assumindo que não vai ficar subindo e descendo
					// A iteração é 2 pois na primeira iteração não dá pra saber se tá subindo (ex:
					// 3 -> 2 -> 3)
					// Se ele fizer (3 -> 2 -> 3, indica o erro abaixo)
					// Se ele fizer (3 -> 2 -> 1 -> 3), ok

					res.setEstruturaCorreta(anterior);
					subindo = true;
					
					pintarErroMenor(erros, anterior.getErroMedio());

				} else if (iteracao > 0 && atual.getErroMedio() > anterior.getErroMedio()) {
					// Subiu na segunda iteração, está errado
					// Pois a taxa de aprendizagem está muito grande
					// tem que ser maior que 0, pois na primeira iteração não há anterior

					throw new ResourceBadRequestException("ERRO! A taxa de aprendizagem está muito alta");
				}
			}

//			// sofisticar: resubmete com uma taxa menor, ai da uma taxa boa

			ErroDTO erroDTO = new ErroDTO();
			erroDTO.setSubindo(subindo);

			CoefsLinhaDTO coefsLinha = new CoefsLinhaDTO();

			if (iteracao == req.getIteracaoMax()) {
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
				res.setEscalaDoGraficoPrincipal(escalaService.gerarEscalaGraficoPrincipal(req, coefsLinhas));

				return res;
			} else {
				// Está achando o erro mínimo, ainda.
				iteracao++;
				anterior = atual;
				erroDTO.setErroMedio(anterior.getErroMedio());
				erroDTO.setIndice(iteracao);
				erros.add(erroDTO);

				atual = new ComparavelModel(anterior.getCoefANovo(), anterior.getCoefBNovo(), iteracao + 1);

				coefsLinha.setCoefA(anterior.getCoefA());
				coefsLinha.setCoefB(anterior.getCoefB());
				coefsLinha.setIndice(iteracao);
				coefsLinhas.add(coefsLinha);

				resetDadosAmostra(req.getAmostra());
			}
		}
	}
	
	public void pintarErroMenor(List<ErroDTO> erros, Double erroMenor) {
		for (int i = erros.size()-1; i >= 0; i--) {
			if (erros.get(i).getErroMedio() == erroMenor) {
				erros.get(i).setMenorErro(true);
				break;
			}
		}
	}

	public ResponseModel acharModeloEPredizer(RequestModel req) {
		ResponseModel res = acharModelo(req);
		ComparavelModel comp = res.getEstruturaCorreta();

		Double predicao = req.getValorXParaPredizer() * comp.getCoefA() + comp.getCoefB();
		res.setYPredicao(predicao);
		res.setEscalaDoGraficoPrincipal(escalaService.gerarEscalaGraficoPrincipal_Predito(req,
				req.getValorXParaPredizer(), predicao, res.getCoefsLinhas()));
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
