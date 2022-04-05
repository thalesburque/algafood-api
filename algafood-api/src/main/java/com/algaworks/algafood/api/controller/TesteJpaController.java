package com.algaworks.algafood.api.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.RestauranteRepository;

@RestController
@RequestMapping("/testes")
public class TesteJpaController {

	@Autowired
	RestauranteRepository restauranteRepository;
	
	@GetMapping("/restaurante-por-nome")
	public List<Restaurante> listarPorNome(@RequestParam String nome, @RequestParam Long idCozinha) {
		return restauranteRepository.consultaPorNome(nome, idCozinha);
	}
	
	@GetMapping("/restaurante-por-nome-query-jpql")
	public List<Restaurante> consultaPorNomeQueryJPQL(@RequestParam String nome, @RequestParam Long idCozinha) {
		return restauranteRepository.consultaPorNomeQueryJPQL(nome, idCozinha);
	}
	
	@GetMapping("/restaurante-por-frete")
	public List<Restaurante> consultaRestaurantePorValorFrete(String nome, BigDecimal taxaFreteMinima, 
			BigDecimal taxaFreteMaxima) {
		return restauranteRepository.findRestaurantePorValorFrete(nome, taxaFreteMinima, taxaFreteMaxima);
	}
	
	@GetMapping("/restaurante-com-frete-gratis")
	public List<Restaurante> consultaRestaurantePorFreteGratis(String nome) {
		return restauranteRepository.findRestaurantePorFreteGratis(nome);
	}
	
	@GetMapping("/primeiro-restaurante")
	public Optional<Restaurante> buscarPrimeiroRestaurante( ) {
		return restauranteRepository.buscarPrimeiro();
	}
}
