package com.algaworks.algafood.domain.exception;

public class PermissaoNaoEncontradoException extends EntidadeNaoEncontradaException{

	private static final long serialVersionUID = 1L;
	
	public PermissaoNaoEncontradoException(String msg) {
		super(msg);
	}
	
	public PermissaoNaoEncontradoException(Long permissaoId) {
		this(String.format("Nao existe cadastro de permissao para o codigo %d", permissaoId));
	}

}
