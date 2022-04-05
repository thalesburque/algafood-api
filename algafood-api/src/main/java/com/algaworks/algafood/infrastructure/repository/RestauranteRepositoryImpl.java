package com.algaworks.algafood.infrastructure.repository;

import static com.algaworks.algafood.infrastructure.repository.Spec.RestauranteSpec.comFreteGratis;
import static com.algaworks.algafood.infrastructure.repository.Spec.RestauranteSpec.comNomeSemelhante;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.RestauranteRepository;
import com.algaworks.algafood.domain.repository.RestauranteRepositoryQueries;

@Repository
public class RestauranteRepositoryImpl implements RestauranteRepositoryQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired @Lazy
	private RestauranteRepository restauranteRepository;

	@Override
	public List<Restaurante> findRestaurantePorValorFrete(String nome, BigDecimal taxaFreteMinima,
			BigDecimal taxaFreteMaxima) {

		/*
		 * Consulta dinamica com JPQL var jpql = new StringBuilder();
		 * jpql.append("from Restaurante where 0 = 0 ");
		 * 
		 * var parameters = new HashMap<String, Object>();
		 * 
		 * if(StringUtils.hasLength(nome)) { jpql.append("and nome like :nome ");
		 * parameters.put("nome", "%" + nome + "%"); }
		 * 
		 * if(taxaFreteMinima != null) { jpql.append("and taxaFrete >= :taxaInicial ");
		 * parameters.put("taxaInicial", taxaFreteMinima); }
		 * 
		 * if(taxaFreteMaxima != null) { jpql.append("and taxaFrete <= :taxaFinal ");
		 * parameters.put("taxaFinal", taxaFreteMaxima); }
		 * 
		 * TypedQuery<Restaurante> query = manager.createQuery(jpql.toString(),
		 * Restaurante.class);
		 * 
		 * parameters.forEach((chave, valor) -> query.setParameter(chave, valor));
		 * 
		 * return query.getResultList();
		 */

		/* Consulta dinamica com criteria API */

		// Contrutor de elementos para realizacao de consultas
		CriteriaBuilder builder = manager.getCriteriaBuilder();

		// Construtor de clausulas SQL
		CriteriaQuery<Restaurante> criteria = builder.createQuery(Restaurante.class);

		//tabela a ser consultada
		Root<Restaurante> root = criteria.from(Restaurante.class);

		var predicatos = new ArrayList<Predicate>();

		if(StringUtils.hasText(nome)) {
			predicatos.add(builder.like(root.get("nome"), "%" + nome + "%"));
		}
		
		if(taxaFreteMinima != null) {
			predicatos.add(builder.greaterThanOrEqualTo(root.get("taxaFrete"), taxaFreteMinima));
		}
		
		if(taxaFreteMaxima != null) {
			predicatos.add(builder.lessThanOrEqualTo(root.get("taxaFrete"), taxaFreteMaxima));
		}
		
		criteria.where(predicatos.toArray(new Predicate[0]));

		TypedQuery<Restaurante> query = manager.createQuery(criteria);

		return query.getResultList();
		
	}

	@Override
	public List<Restaurante> findRestaurantePorFreteGratis(String nome) {
		return restauranteRepository.findAll(comFreteGratis().and(comNomeSemelhante(nome)));
	}
	
	

}
