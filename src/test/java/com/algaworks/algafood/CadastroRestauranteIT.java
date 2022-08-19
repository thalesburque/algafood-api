package com.algaworks.algafood;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.CozinhaRepository;
import com.algaworks.algafood.domain.repository.RestauranteRepository;
import com.algaworks.algafood.util.DatabaseCleaner;
import com.algaworks.algafood.util.ResourceUtils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
public class CadastroRestauranteIT {
	
	private static final String NOME_RESTAURANTE_ATUALIZADO = "Gendai";
	private static final long RESTAURANTE_ID_INEXISTENTE = 100L;
	
	private static final String ERRO_NEGOCIO_PROBLEM_TYPE = "Violacao da regra de negocio";
	private static final String DADOS_INVALIDOS_PROBLEM_TYPE = "Dados inválidos";
	private static final String MENSAGEM_INCOMPREENSIVEL_PROBLEM_TYPE = "Mensagem incompreensível";
	private static final String RECURSO_NAO_ENCONTRADO = "Recurso não encontrado";
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private DatabaseCleaner databaseCleaner;
	
	@Autowired
	private CozinhaRepository cozinhaRepository;
	
	@Autowired
	private RestauranteRepository restauranteRepository;
	
	private String jsonCadastroRestaurante;
	private String jsonAtualizacaoRestaurante;
	private String jsonCadastroRestauranteCozinhaInvalida;
	private String jsonCadastroRestauranteSemCozinha;
	private String jsonCadastroRestauranteComCampoInvalido;
	
	private int quantidadeRestauranteCadastrado;
	
	private Restaurante restaurante;
	
	@Before
	public void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/restaurantes";
		
		jsonCadastroRestaurante = ResourceUtils.getContentFromResource("/json/correto/restaurante-cadastro.json");
		jsonAtualizacaoRestaurante = ResourceUtils.getContentFromResource("/json/correto/restaurante-atualizacao.json");
		jsonCadastroRestauranteCozinhaInvalida = ResourceUtils.getContentFromResource("/json/incorreto/restaurante-cadastro-cozinha-invalida.json");
		jsonCadastroRestauranteSemCozinha = ResourceUtils.getContentFromResource("/json/incorreto/restaurante-cadastro-sem-cozinha.json");
		jsonCadastroRestauranteComCampoInvalido = ResourceUtils.getContentFromResource("/json/incorreto/restaurante-cadastro-campo-invalido.json");
		
		databaseCleaner.clearTables();
		prepararDados();
	}
	
	@Test
	public void deveRetornarStatus201_quandoCadastrarRestaurante() {
		given()
			.body(jsonCadastroRestaurante)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.CREATED.value());
	}
	
	@Test
	public void deveRetornarStatus200_quandoAtualizarRestaurante() {
		given()
			.pathParam("restauranteId", restaurante.getId())
			.body(jsonAtualizacaoRestaurante)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{restauranteId}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("nome", equalTo(NOME_RESTAURANTE_ATUALIZADO));
	}
	
	@Test
	public void deveRetornarStatus400_quandoCadastrarRestauranteComCozinhaInvalida() {
		given()
			.body(jsonCadastroRestauranteCozinhaInvalida)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("title", equalTo(ERRO_NEGOCIO_PROBLEM_TYPE));
	}
	
	@Test
	public void deveRetornarStatus400_quandoCadastrarRestauranteSemCozinha() {
		given()
			.body(jsonCadastroRestauranteSemCozinha)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("title", equalTo(DADOS_INVALIDOS_PROBLEM_TYPE));
	}
	
	@Test
	public void deveRetornarStatus400_quandoCadastrarRestauranteCampoInvalido() {
		given()
			.body(jsonCadastroRestauranteComCampoInvalido)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("title", equalTo(MENSAGEM_INCOMPREENSIVEL_PROBLEM_TYPE));
	}
	
	@Test
	public void deveRetornarStatus200ETotalDeRestaurantesCadastrados_quandoConsultarRestaurantes() {
		given()
			.accept(ContentType.JSON)
		.when()
			.get()
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("", hasSize(quantidadeRestauranteCadastrado));
	}
	
	@Test
	public void deveRetornarStatus200EConteudoCorretos_quandoConsultarRestauranteExistente() {
		given()
			.pathParam("restauranteId", restaurante.getId())
			.accept(ContentType.JSON)
		.when()
			.get("/{restauranteId}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("nome", equalTo(restaurante.getNome()));
	}
	
	@Test
	public void deveRetornarStatus404_quandoConsultarRestauranteInexistente() {
		given()
			.pathParam("restauranteId", RESTAURANTE_ID_INEXISTENTE)
			.accept(ContentType.JSON)
		.when()
			.get("/{restauranteId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("title", equalTo(RECURSO_NAO_ENCONTRADO));
	}
	
	private void prepararDados() {
		Cozinha novaCozinha = new Cozinha();
		novaCozinha.setNome("Japonesa");
		
		novaCozinha = cozinhaRepository.save(novaCozinha);
		
		restaurante = new Restaurante();
		restaurante.setNome("Hirota Food");
		restaurante.setTaxaFrete(BigDecimal.valueOf(10));
		restaurante.setCozinha(novaCozinha);
		
		restauranteRepository.save(restaurante);
		
		quantidadeRestauranteCadastrado = (int) restauranteRepository.count();
		
	}

}
