package com.algaworks.algafood.domain.service;

import java.io.InputStream;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.algafood.domain.exception.FotoProdutoNaoEncontradaException;
import com.algaworks.algafood.domain.model.FotoProduto;
import com.algaworks.algafood.domain.repository.ProdutoRepository;
import com.algaworks.algafood.domain.service.FotoStorageService.NovaFoto;

@Service
public class CatalogoFotoProdutoService {

	@Autowired
	ProdutoRepository produtoRepository;

	@Autowired
	FotoStorageService fotoStorage;

	@Transactional
	public FotoProduto salvar(FotoProduto foto, InputStream dadosArquivo) {

		Long produtoId = foto.getProduto().getId();
		Long restauranteId = foto.getProduto().getRestaurante().getId();
		String nomeNovoArquivo = fotoStorage.gerarNomeArquivo(foto.getNomeArquivo());
		String nomeArquivoAntigo = null;

		Optional<FotoProduto> fotoExistente = produtoRepository.findFotoById(produtoId, restauranteId);

		if (fotoExistente.isPresent()) {
			nomeArquivoAntigo = fotoExistente.get().getNomeArquivo();
			produtoRepository.delete(fotoExistente.get());
		}

		foto.setNomeArquivo(nomeNovoArquivo);
		foto = produtoRepository.save(foto);
		produtoRepository.flush();

		NovaFoto novaFoto = NovaFoto.builder().nomeArquivo(nomeNovoArquivo).contentType(foto.getContentType())
				.inputStream(dadosArquivo).build();

		fotoStorage.substituir(nomeArquivoAntigo, novaFoto);

		return foto;
	}

	@Transactional
	public void excluir(Long produtoId, Long restauranteId) {

		FotoProduto foto = this.buscarComTratamentoErro(produtoId, restauranteId);

		produtoRepository.delete(foto);
		produtoRepository.flush();

		fotoStorage.remover(foto.getNomeArquivo());

	}

	public FotoProduto buscarComTratamentoErro(Long produtoId, Long restauranteId) {
		return produtoRepository.findFotoById(produtoId, restauranteId)
				.orElseThrow(() -> new FotoProdutoNaoEncontradaException(produtoId, restauranteId));
	}

}
