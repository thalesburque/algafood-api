package com.algaworks.algafood.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.algafood.domain.exception.NegocioException;
import com.algaworks.algafood.domain.exception.PedidoNaoEncontradoException;
import com.algaworks.algafood.domain.model.Cidade;
import com.algaworks.algafood.domain.model.FormaPagamento;
import com.algaworks.algafood.domain.model.Pedido;
import com.algaworks.algafood.domain.model.Produto;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.model.Usuario;
import com.algaworks.algafood.domain.repository.PedidoRepository;

@Service
public class EmissaoPedidoService {

	@Autowired
	PedidoRepository pedidoRepository;

	@Autowired
	CadastroRestauranteService cadastroRestaurante;

	@Autowired
	CadastroFormaPagamentoService cadastroFormaPagamento;

	@Autowired
	CadastroCidadeService cadastroCidade;

	@Autowired
	CadastroUsuarioService cadastroUsuario;

	@Autowired
	CadastroProdutoService cadastroProduto;

	@Transactional
	public Pedido emitir(Pedido pedido) {

		validarPedido(pedido);
		validarItens(pedido);

		pedido.setTaxaFrete(pedido.getRestaurante().getTaxaFrete());
		pedido.calcularValorTotal();

		return pedidoRepository.save(pedido);
	}

	public Pedido buscarComTratamentoErro(String codigoPedido) {
		return pedidoRepository.findByCodigo(codigoPedido).orElseThrow(() -> new PedidoNaoEncontradoException(
				String.format("Nao existe cadastro de pedido para o codigo %s", codigoPedido)));
	}

	private void validarPedido(Pedido pedido) {
		Restaurante restaurante = cadastroRestaurante.buscarComTratamentoErro(pedido.getRestaurante().getId());
		FormaPagamento formaPagamento = cadastroFormaPagamento
				.buscarComTratamentoErro(pedido.getFormaPagamento().getId());
		Cidade cidade = cadastroCidade.buscarComTratamentoErro(pedido.getEnderecoEntrega().getCidade().getId());
		Usuario cliente = cadastroUsuario.buscarComTratamentoErro(pedido.getCliente().getId());

		if (!restaurante.aceitaFormaPagamento(formaPagamento)) {
			throw new NegocioException(String.format("Restaurante de id %d não aceita a forma de pagamento %s",
					restaurante.getId(), formaPagamento.getDescricao()));
		}

		pedido.setRestaurante(restaurante);
		pedido.setFormaPagamento(formaPagamento);
		pedido.getEnderecoEntrega().setCidade(cidade);
		pedido.setCliente(cliente);

	}

	private void validarItens(Pedido pedido) {

		pedido.getItens().forEach(item -> {
			Produto produto = cadastroProduto.buscarComtratamentoErro(item.getProduto().getId(),
					pedido.getRestaurante().getId());

			if (!produto.getAtivo()) {
				throw new NegocioException(String.format("Produto de id %d não está ativo", produto.getId()));
			}

			item.setProduto(produto);
			item.setPedido(pedido);
			item.setPrecoUnitario(produto.getPreco());
		});

	}
}
