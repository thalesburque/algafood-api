package com.algaworks.algafood.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algafood.api.assembler.ProdutoInputDisassembler;
import com.algaworks.algafood.api.assembler.ProdutoModelAssembler;
import com.algaworks.algafood.api.model.ProdutoModel;
import com.algaworks.algafood.api.model.input.ProdutoInput;
import com.algaworks.algafood.domain.model.Produto;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.ProdutoRepository;
import com.algaworks.algafood.domain.service.CadastroProdutoService;
import com.algaworks.algafood.domain.service.CadastroRestauranteService;

@RestController
@RequestMapping("/restaurantes/{restauranteId}/produtos")
public class RestauranteProdutoController {

	@Autowired
	ProdutoRepository produtoRepository;

	@Autowired
	CadastroProdutoService cadastroProduto;

	@Autowired
	CadastroRestauranteService cadastroRestaurante;

	@Autowired
	ProdutoModelAssembler produtoModelAssembler;

	@Autowired
	ProdutoInputDisassembler produtoInputDisassembler;

	@GetMapping
	public List<ProdutoModel> listar(@PathVariable Long restauranteId,
			@RequestParam(required = false) boolean incluirInativos) {

		Restaurante restaurante = cadastroRestaurante.buscarComTratamentoErro(restauranteId);

		List<Produto> produtos = null;

		if (incluirInativos) {
			produtos = produtoRepository.findByRestaurante(restaurante);
		} else {
			produtos = produtoRepository.findAtivosByRestaurante(restaurante);
		}

		return produtoModelAssembler.toCollectionModel(produtos);

	}

	@GetMapping("/{produtoId}")
	public ProdutoModel buscar(@PathVariable Long restauranteId, @PathVariable Long produtoId) {

		Produto produto = cadastroProduto.buscarComtratamentoErro(produtoId, restauranteId);

		return produtoModelAssembler.toModel(produto);

	}

	@PostMapping
	public ProdutoModel adicionar(@PathVariable Long restauranteId, @RequestBody @Valid ProdutoInput produtoInput) {

		Restaurante restaurante = cadastroRestaurante.buscarComTratamentoErro(restauranteId);

		Produto produto = produtoInputDisassembler.toDomainObject(produtoInput);

		produto.setRestaurante(restaurante);

		return produtoModelAssembler.toModel(cadastroProduto.salvar(produto));

	}

	@PutMapping("/{produtoId}")
	public ProdutoModel atualizar(@PathVariable Long restauranteId, @PathVariable Long produtoId,
			@RequestBody @Valid ProdutoInput produtoInput) {

		Produto produtoAtual = cadastroProduto.buscarComtratamentoErro(produtoId, restauranteId);

		produtoInputDisassembler.copyToDomainObject(produtoInput, produtoAtual);

		produtoAtual = cadastroProduto.salvar(produtoAtual);

		return produtoModelAssembler.toModel(produtoAtual);

	}

}
