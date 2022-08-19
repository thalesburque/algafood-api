package com.algaworks.algafood.domain.repository;

import java.math.BigDecimal;
import java.util.List;

import com.algaworks.algafood.domain.model.Restaurante;

public interface RestauranteRepositoryQueries {

	List<Restaurante> findRestaurantePorValorFrete(String nome, BigDecimal taxaFreteMinima,
			BigDecimal taxaFreteMaxima);
	
	List<Restaurante> findRestaurantePorFreteGratis(String nome);

}