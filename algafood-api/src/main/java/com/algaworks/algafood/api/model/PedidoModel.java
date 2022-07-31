package com.algaworks.algafood.api.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.algaworks.algafood.domain.model.enums.StatusPedido;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PedidoModel {
	
	private String codigo;

	private BigDecimal subtotal;

	private BigDecimal taxaFrete;

	private BigDecimal valorTotal;
	
	private StatusPedido status;

	private OffsetDateTime dataCriacao;

	private OffsetDateTime dataConfirmacao;

	private OffsetDateTime dataCancelamento;

	private OffsetDateTime dataEntrega;

	private EnderecoModel enderecoEntrega;

	private UsuarioModel cliente;
	
	private RestauranteResumoModel restaurante;
	
	private FormaPagamentoModel formaPagamento;

	private List<ItemPedidoModel> itens = new ArrayList<>();

}
