package com.algaworks.algafood.domain.exception;

public class ProdutoNaoEncontradoException extends EntidadeNaoEncontradaException{

	private static final long serialVersionUID = 1L;

	public ProdutoNaoEncontradoException(String msg) {
		super(msg);
	}
	
	public ProdutoNaoEncontradoException(Long produtoId, Long restauranteId) {
		super(String.format("Não existe cadastro de produto com o código %d para o restaurante de código %d", produtoId, restauranteId));
	}
	
}
