package com.algaworks.algafood.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity()
public class Restaurante {

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nome;

	@Column(name = "taxa_frete", nullable = false)
	private BigDecimal taxaFrete;

	@Column(nullable = false)
	private Boolean ativo = Boolean.TRUE;

	@Column(nullable = false)
	private Boolean aberto = Boolean.FALSE;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Cozinha cozinha;

	@Embedded
	private Endereco endereco;

	@CreationTimestamp
	@Column(nullable = false, columnDefinition = "datetime")
	private OffsetDateTime dataCadastro;

	@UpdateTimestamp
	@Column(nullable = false, columnDefinition = "datetime")
	private OffsetDateTime dataAtualizacao;

	@ManyToMany
	@JoinTable(name = "restaurante_forma_pagamento", joinColumns = @JoinColumn(name = "restaurante_id"), inverseJoinColumns = @JoinColumn(name = "forma_pagamento_id"))
	private Set<FormaPagamento> formasPagamento = new HashSet<>();

	@OneToMany(mappedBy = "restaurante")
	private List<Produto> produtos;

	@ManyToMany
	@JoinTable(name = "restaurante_usuario_responsavel", joinColumns = @JoinColumn(name = "restaurante_id"), inverseJoinColumns = @JoinColumn(name = "usuario_id"))
	private Set<Usuario> responsaveis = new HashSet<>();

	public void ativar() {
		setAtivo(true);
	}

	public void inativar() {
		setAtivo(false);
	}

	public void adicionarFormaPagamento(FormaPagamento formaPagamento) {
		formasPagamento.add(formaPagamento);
	}

	public void removerFormaPagamento(FormaPagamento formaPagamento) {
		formasPagamento.remove(formaPagamento);
	}

	public void abrir() {
		setAberto(true);
	}

	public void fechar() {
		setAberto(false);
	}

	public void adicionarResponsavel(Usuario usuario) {
		responsaveis.add(usuario);
	}

	public void removerResponsavel(Usuario usuario) {
		responsaveis.remove(usuario);
	}
	
	public boolean aceitaFormaPagamento(FormaPagamento formaPagamento) {
		
		for(FormaPagamento f : this.formasPagamento ) {
			if(f.getId().compareTo(formaPagamento.getId()) == 0) {
				return true;
			}
		}
		
		return false;
		
	}

}
