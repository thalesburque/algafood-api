package com.algaworks.algafood.domain.exception;

public class UsuarioNaoEncontradoException extends EntidadeNaoEncontradaException{

private static final long serialVersionUID = 1L;
	
	public UsuarioNaoEncontradoException(String msg) {
		super(msg);
	}
	
	public UsuarioNaoEncontradoException(Long usuarioId) {
		this(String.format("Nao existe cadastro de usuario para o codigo %d", usuarioId));
	}
	
}
