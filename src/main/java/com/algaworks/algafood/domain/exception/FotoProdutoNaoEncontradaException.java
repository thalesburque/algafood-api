package com.algaworks.algafood.domain.exception;

public class FotoProdutoNaoEncontradaException extends EntidadeNaoEncontradaException{

	private static final long serialVersionUID = 1L;

	public FotoProdutoNaoEncontradaException(String msg) {
		super(msg);
	}
	
	public FotoProdutoNaoEncontradaException(Long produtoId, Long restauranteId) {
		super(String.format("Não existe cadastro de foto para produto de código %d do restaurante de código %d", produtoId, restauranteId));
	}
	
}
