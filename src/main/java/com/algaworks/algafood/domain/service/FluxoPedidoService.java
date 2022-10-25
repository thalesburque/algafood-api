package com.algaworks.algafood.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.algafood.domain.model.Pedido;
import com.algaworks.algafood.domain.repository.PedidoRepository;

@Service
public class FluxoPedidoService {

	@Autowired
	EmissaoPedidoService emissaoPedido;
	
	@Autowired
	PedidoRepository pedidoRepository;

	@Transactional
	public void confirmar(String codigoPedido) {

		Pedido pedido = emissaoPedido.buscarComTratamentoErro(codigoPedido);

		pedido.confirmar();
		
		pedidoRepository.save(pedido);

	}

	@Transactional
	public void entregar(String codigoPedido) {

		Pedido pedido = emissaoPedido.buscarComTratamentoErro(codigoPedido);

		pedido.entregar();

	}

	@Transactional
	public void cancelar(String codigoPedido) {

		Pedido pedido = emissaoPedido.buscarComTratamentoErro(codigoPedido);

		pedido.cancelar();
		
		pedidoRepository.save(pedido);

	}

}
