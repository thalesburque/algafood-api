package com.algaworks.algafood.domain.exception;

public class GrupoNaoEncontradoException extends EntidadeNaoEncontradaException{

private static final long serialVersionUID = 1L;
	
	public GrupoNaoEncontradoException(String msg) {
		super(msg);
	}
	
	public GrupoNaoEncontradoException(Long grupoId) {
		this(String.format("Nao existe cadastro de grupo para o codigo %d", grupoId));
	}
}
