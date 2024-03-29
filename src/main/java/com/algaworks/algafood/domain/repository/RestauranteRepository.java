package com.algaworks.algafood.domain.repository; 

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.algaworks.algafood.domain.model.Restaurante;

@Repository
public interface RestauranteRepository extends CustomJpaRespository<Restaurante, Long>, RestauranteRepositoryQueries, JpaSpecificationExecutor<Restaurante> {

	@Query("select distinct r from Restaurante r left join fetch r.cozinha left join fetch r.formasPagamento")
	List<Restaurante> findAll();
	
	List<Restaurante> findByTaxaFreteBetween(BigDecimal taxaInicial, BigDecimal taxaFinal);
	
	List<Restaurante> findByNomeContainingAndCozinhaId(String nome, Long cozinhaId);
	
	@Query("from Restaurante where nome like %:nome% and cozinha.id = :cozinha")
	List<Restaurante> consultaPorNomeQueryJPQL(String nome, Long cozinha);
	
	List<Restaurante> consultaPorNome(String nome,@Param("id") Long cozinhaId);
	
}
