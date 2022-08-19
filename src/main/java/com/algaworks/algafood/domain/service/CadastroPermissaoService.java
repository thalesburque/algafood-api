package com.algaworks.algafood.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.algaworks.algafood.domain.exception.PermissaoNaoEncontradoException;
import com.algaworks.algafood.domain.model.Permissao;
import com.algaworks.algafood.domain.repository.PermissaoRepository;

@Service
public class CadastroPermissaoService {

	@Autowired
	private PermissaoRepository permissaoRepository;
	
	public Permissao buscarComTratamentoErro(Long permissaoId) {
		return permissaoRepository.findById(permissaoId).orElseThrow(() -> new PermissaoNaoEncontradoException(permissaoId));
	}
}
