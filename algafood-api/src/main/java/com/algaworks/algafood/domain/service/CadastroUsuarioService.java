package com.algaworks.algafood.domain.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
import com.algaworks.algafood.domain.exception.NegocioException;
import com.algaworks.algafood.domain.exception.UsuarioNaoEncontradoException;
import com.algaworks.algafood.domain.model.Grupo;
import com.algaworks.algafood.domain.model.Usuario;
import com.algaworks.algafood.domain.repository.UsuarioRepository;

@Service
public class CadastroUsuarioService {
	
	private static final String MSG_USUARIO_EM_USO = "Usuario de codigo %d nao pode ser removido, pois esta em uso";
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private CadastroGrupoService cadastroGrupo;
	
	@Transactional
	public Usuario salvar(Usuario usuario) {
		
		//remove usuario desserializado no controller do contexto do entityManager 
		//para que em caso de erro o objeto nao seja commitado apos a transacao
		usuarioRepository.detach(usuario);
		
		Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(usuario.getEmail());
		
		if(usuarioExistente.isPresent() && !usuarioExistente.get().equals(usuario)) {
			throw new NegocioException(String.format("Já existe um usuário cadastrado com o e-mail %s", usuario.getEmail()));
		}
		
		return usuarioRepository.save(usuario);
	}
	
	@Transactional
	public void excluir(Long usuarioId) {
		try {
			usuarioRepository.deleteById(usuarioId);
			usuarioRepository.flush();
			
		} catch(EmptyResultDataAccessException e) {
			throw new UsuarioNaoEncontradoException(usuarioId);
		} catch(DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(String.format(MSG_USUARIO_EM_USO, usuarioId));
		} 
	}
	
	@Transactional
	public void alterarSenha(Long usuarioId, String senhaAtual, String novaSenha) {
		
		Usuario usuario = buscarComTratamentoErro(usuarioId);
		
		if(usuario.senhaNaoCoincideCom(senhaAtual)) {
			throw new NegocioException("Senha atual informada não coincide com a senha do usuário.");
		} 
		
		usuario.setSenha(novaSenha);
		
	}
	
	@Transactional
	public void associarGrupo(Long usuarioId, Long grupoId) {
		
		Usuario usuario = buscarComTratamentoErro(usuarioId);
		
		Grupo grupo = cadastroGrupo.buscarComTratamentoErro(grupoId);
		
		usuario.adicionarGrupo(grupo);
		
	}
	
	@Transactional
	public void desassociarGrupo(Long usuarioId, Long grupoId) {
		
		Usuario usuario = buscarComTratamentoErro(usuarioId);
		
		Grupo grupo = cadastroGrupo.buscarComTratamentoErro(grupoId);
		
		usuario.removerGrupo(grupo);
		
	}
	
	public Usuario buscarComTratamentoErro(Long usuarioId) {
		return usuarioRepository.findById(usuarioId).orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioId));
	}

}
