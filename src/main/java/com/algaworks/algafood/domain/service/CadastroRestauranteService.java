package com.algaworks.algafood.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
import com.algaworks.algafood.domain.exception.NegocioException;
import com.algaworks.algafood.domain.exception.RestauranteNaoEncontradoException;
import com.algaworks.algafood.domain.model.Cidade;
import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.model.FormaPagamento;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.model.Usuario;
import com.algaworks.algafood.domain.repository.RestauranteRepository;

@Service
public class CadastroRestauranteService {

	private static final String MSG_RESTAURANTE_EM_USO = "Restaurante de codigo %d nao pode ser removido, pois esta em uso";

	@Autowired
	private RestauranteRepository restauranteRepository;

	@Autowired
	private CadastroCozinhaService cadastroCozinha;

	@Autowired
	private CadastroCidadeService cadastroCidade;

	@Autowired
	private CadastroFormaPagamentoService cadastroFormaPagamento;

	@Autowired
	private CadastroUsuarioService cadastroUsuario;

	@Transactional
	public Restaurante salvar(Restaurante restaurante) {

		Long cozinhaId = restaurante.getCozinha().getId();
		Cozinha cozinha = cadastroCozinha.buscarComTratamentoErro(cozinhaId);

		restaurante.setCozinha(cozinha);

		Long cidadeId = restaurante.getEndereco().getCidade().getId();
		Cidade cidade = cadastroCidade.buscarComTratamentoErro(cidadeId);

		restaurante.getEndereco().setCidade(cidade);

		return restauranteRepository.save(restaurante);
	}

	@Transactional
	public void excluir(Long restauranteId) {
		try {
			restauranteRepository.deleteById(restauranteId);
			restauranteRepository.flush();

		} catch (EmptyResultDataAccessException e) {
			throw new RestauranteNaoEncontradoException(restauranteId);

		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(String.format(MSG_RESTAURANTE_EM_USO, restauranteId));
		}
	}

	@Transactional
	public void ativar(Long restauranteId) {

		Restaurante restauranteAtual = buscarComTratamentoErro(restauranteId);

		restauranteAtual.ativar();
	}

	@Transactional
	public void inativar(Long restauranteId) {

		Restaurante restauranteAtual = buscarComTratamentoErro(restauranteId);

		restauranteAtual.inativar();
	}

	@Transactional
	public void ativarEmMassa(List<Long> restauranteIds) {
		try {
			restauranteIds.forEach(this::ativar);
		} catch (RestauranteNaoEncontradoException e) {
			throw new NegocioException(e.getMessage());
		}
	}

	@Transactional
	public void inativarEmMassa(List<Long> restauranteIds) {
		try {
			restauranteIds.forEach(this::inativar);
		} catch (RestauranteNaoEncontradoException e) {
			throw new NegocioException(e.getMessage());
		}
	}

	@Transactional
	public void associarFormaPagamento(Long restaurateId, Long formaPagamentoId) {

		Restaurante restaurante = buscarComTratamentoErro(restaurateId);

		FormaPagamento formaPagamento = cadastroFormaPagamento.buscarComTratamentoErro(formaPagamentoId);

		restaurante.adicionarFormaPagamento(formaPagamento);

	}

	@Transactional
	public void desassociarFormaPagamento(Long restaurateId, Long formaPagamentoId) {

		Restaurante restaurante = buscarComTratamentoErro(restaurateId);

		FormaPagamento formaPagamento = cadastroFormaPagamento.buscarComTratamentoErro(formaPagamentoId);

		restaurante.removerFormaPagamento(formaPagamento);

	}

	@Transactional
	public void abrir(Long restauranteId) {

		Restaurante restaurante = buscarComTratamentoErro(restauranteId);

		restaurante.abrir();

	}

	@Transactional
	public void fechar(Long restauranteId) {

		Restaurante restaurante = buscarComTratamentoErro(restauranteId);

		restaurante.fechar();

	}

	@Transactional
	public void associarResponsavel(Long restauranteId, Long usuarioId) {

		Restaurante restaurante = buscarComTratamentoErro(restauranteId);

		Usuario usuario = cadastroUsuario.buscarComTratamentoErro(usuarioId);

		restaurante.adicionarResponsavel(usuario);

	}

	@Transactional
	public void desassociarResponsavel(Long restauranteId, Long usuarioId) {

		Restaurante restaurante = buscarComTratamentoErro(restauranteId);

		Usuario usuario = cadastroUsuario.buscarComTratamentoErro(usuarioId);

		restaurante.removerResponsavel(usuario);

	}

	public Restaurante buscarComTratamentoErro(Long restauranteId) {

		return restauranteRepository.findById(restauranteId)
				.orElseThrow(() -> new RestauranteNaoEncontradoException(restauranteId));

	}

}
