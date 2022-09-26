package com.algaworks.algafood.domain.service;

import java.io.InputStream;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

public interface FotoStorageService {
	
	FotoRecuperada recuperar(String nomeArquivo);

	void armazenar(NovaFoto novaFoto);

	void remover(String nomeArquivo);

	default void substituir(String nomeArquivoAntigo, NovaFoto novaFoto) {
		this.armazenar(novaFoto);

		if (nomeArquivoAntigo != null) {
			this.remover(nomeArquivoAntigo);
		}
	}

	default String gerarNomeArquivo(String nomeArquivo) {
		return UUID.randomUUID().toString() + "_" + nomeArquivo;
	}

	@Builder
	@Getter
	public class NovaFoto {
		private String nomeArquivo;
		private String contentType;
		private InputStream inputStream;
	}
	
	@Builder
	@Getter
	public class FotoRecuperada {
		private InputStream inputStream;
		private String url;
		
		public boolean temUrl() {
			return this.url != null;
		}
		
		public boolean temInputStream() {
			return this.inputStream != null;
		}
	}

}
