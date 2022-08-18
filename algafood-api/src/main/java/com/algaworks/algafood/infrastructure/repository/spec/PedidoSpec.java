package com.algaworks.algafood.infrastructure.repository.spec;

import java.util.ArrayList;

import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.algaworks.algafood.domain.filter.PedidoFilter;
import com.algaworks.algafood.domain.model.Pedido;

public class PedidoSpec {

	public static Specification<Pedido> usandoFiltro(PedidoFilter filter) {

		return (root, query, criteriaBuilder) -> {
			
			if (Pedido.class.equals(query.getResultType())) {
				root.fetch("restaurante").fetch("cozinha");
				root.fetch("cliente");
			}

			var predicates = new ArrayList<Predicate>();

			if (filter.getClienteId() != null) {
				predicates.add(criteriaBuilder.equal(root.get("cliente"), filter.getClienteId()));
			}

			if (filter.getRestauranteId() != null) {
				predicates.add(criteriaBuilder.equal(root.get("restaurante"), filter.getRestauranteId()));
			}

			if (filter.getDataCriacaoInicio() != null) {
				predicates.add(
						criteriaBuilder.greaterThanOrEqualTo(root.get("dataCriacao"), filter.getDataCriacaoInicio()));
			}

			if (filter.getDataCriacaoFim() != null) {
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dataCriacao"), filter.getDataCriacaoFim()));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

		};

	}

}
