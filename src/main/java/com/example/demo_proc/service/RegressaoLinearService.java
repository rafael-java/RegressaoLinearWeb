package com.example.demo_proc.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo_proc.error_exception.ResourceBadRequestException;
import com.example.demo_proc.models.ComparavelModel;
import com.example.demo_proc.models.DadoModel;
import com.example.demo_proc.models.ErrosDTO;
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
		List<ErrosDTO> memoria = new ArrayList<ErrosDTO>();

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
		
			if (iteracao == re.getIteracaoMax()) {
//				// ao invez de iteracao maxima, colocar um limite, como se fosse um delta
				// Não achou o erro mínimo, mas chegou na iteração máxima
				// IteracaoMax = quando para
				erroDTO.setErroMedio(atual.getErroMedio());
				erroDTO.setIndice(iteracao+1);
				memoria.add(erroDTO);
				res.setErros(memoria);
				return res;
			} else {
				// Está achando o erro mínimo, ainda.
				iteracao++;
				anterior = atual;
				erroDTO.setErroMedio(anterior.getErroMedio());
				erroDTO.setIndice(iteracao);
				memoria.add(erroDTO);
				
				atual = new ComparavelModel(anterior.getCoefANovo(), anterior.getCoefBNovo());
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
		return res;
	}

}
