package com.algaworks.algafood.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.AbstractAggregateRoot;

import com.algaworks.algafood.domain.event.PedidoCanceladoEvent;
import com.algaworks.algafood.domain.event.PedidoConfirmadoEvent;
import com.algaworks.algafood.domain.exception.NegocioException;
import com.algaworks.algafood.domain.model.enums.StatusPedido;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
public class Pedido extends AbstractAggregateRoot<Pedido>{

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String codigo;

	@Column(nullable = false)
	private BigDecimal subtotal;

	@Column(nullable = false)
	private BigDecimal taxaFrete;

	@Column(nullable = false)
	private BigDecimal valorTotal;

	@CreationTimestamp
	@Column(nullable = false, columnDefinition = "datetime")
	private OffsetDateTime dataCriacao;

	@Column(columnDefinition = "datetime")
	private OffsetDateTime dataConfirmacao;

	@Column(columnDefinition = "datetime")
	private OffsetDateTime dataCancelamento;

	@Column(columnDefinition = "datetime")
	private OffsetDateTime dataEntrega;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private StatusPedido status = StatusPedido.CRIADO;

	@Embedded
	private Endereco enderecoEntrega;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Usuario cliente;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Restaurante restaurante;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private FormaPagamento formaPagamento;

	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
	private List<ItemPedido> itens = new ArrayList<>();

	public void calcularValorTotal() {

		getItens().forEach(ItemPedido::calcularPrecoTotal);

		this.subtotal = getItens().stream().map(item -> item.getPrecoTotal()).reduce(BigDecimal.ZERO, BigDecimal::add);

		this.setValorTotal(this.subtotal.add(this.taxaFrete));

	}

	public void confirmar() {

		this.setStatus(StatusPedido.CONFIRMADO);
		this.setDataConfirmacao(OffsetDateTime.now());
		
		registerEvent(new PedidoConfirmadoEvent(this));

	}

	public void entregar() {

		this.setStatus(StatusPedido.ENTREGUE);
		this.setDataEntrega(OffsetDateTime.now());

	}

	public void cancelar() {
		
		this.setStatus(StatusPedido.CANCELADO);
		this.setDataCancelamento(OffsetDateTime.now());
		
		registerEvent(new PedidoCanceladoEvent(this));

	}

	private void setStatus(StatusPedido novoStatus) {
		if (this.getStatus().naoPodeAlterarStatus(novoStatus)) {
			throw new NegocioException(String.format("Status do pedido de codigo %s não pode ser alterado de %s para %s",
					this.getCodigo(), this.getStatus().getDescricao(), novoStatus.getDescricao()));
		}

		this.status = novoStatus;

	}
	
	@PrePersist
	private void gerarCodigo() {
		this.setCodigo(UUID.randomUUID().toString());
	}

}
