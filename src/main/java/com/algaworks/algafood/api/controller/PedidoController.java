package com.algaworks.algafood.api.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algafood.api.assembler.PedidoInputDisassembler;
import com.algaworks.algafood.api.assembler.PedidoModelAssembler;
import com.algaworks.algafood.api.assembler.PedidoResumoModelAssembler;
import com.algaworks.algafood.api.model.PedidoModel;
import com.algaworks.algafood.api.model.PedidoResumoModel;
import com.algaworks.algafood.api.model.input.PedidoInput;
import com.algaworks.algafood.core.data.PageableTranslator;
import com.algaworks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.exception.NegocioException;
import com.algaworks.algafood.domain.filter.PedidoFilter;
import com.algaworks.algafood.domain.model.Pedido;
import com.algaworks.algafood.domain.model.Usuario;
import com.algaworks.algafood.domain.repository.PedidoRepository;
import com.algaworks.algafood.domain.service.EmissaoPedidoService;
import com.algaworks.algafood.infrastructure.repository.spec.PedidoSpec;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {
	
	@Autowired
	PedidoRepository pedidoRepository;
	
	@Autowired
	EmissaoPedidoService emissaoPedido;
	
	@Autowired
	PedidoModelAssembler pedidoModelAssembler;
	
	@Autowired
	PedidoResumoModelAssembler pedidoResumoModelAssembler;
	
	@Autowired
	PedidoInputDisassembler pedidoInputDisassembler;
	
	/* busca filtrando o payload de retorno com os parametros enviado pelo cliente
	@GetMapping
	public MappingJacksonValue listar(@RequestParam(required = false) String campos) {
		List<Pedido> pedidos = pedidoRepository.findAll();
		List<PedidoResumoModel> pedidosModel = pedidoResumoModelAssembler.toCollectionModel(pedidos);  
		
		MappingJacksonValue pedidosWrapper = new MappingJacksonValue(pedidosModel);
		
		SimpleFilterProvider filterProvider = new SimpleFilterProvider();
		filterProvider.addFilter("pedidoFilter", SimpleBeanPropertyFilter.serializeAll());
		
		if (StringUtils.isNotBlank(campos)) {
			filterProvider.addFilter("pedidoFilter", SimpleBeanPropertyFilter.filterOutAllExcept(campos.split(",")));
		}
		
		pedidosWrapper.setFilters(filterProvider);
		
		return pedidosWrapper;
	}
	*/
	
	@GetMapping
	public Page<PedidoResumoModel> pesquisar(PedidoFilter filtro, @PageableDefault(size = 10) Pageable pageable) {
		
		pageable = traduzirPageable(pageable);
		
		Page<Pedido> pedidosPage = pedidoRepository.findAll(PedidoSpec.usandoFiltro(filtro), pageable);
		
		List<PedidoResumoModel> pedidosModel = pedidoResumoModelAssembler.toCollectionModel(pedidosPage.getContent());
		
		Page<PedidoResumoModel> pedidosModelPage = new PageImpl<>(pedidosModel, pageable, pedidosPage.getTotalElements());
		
		return pedidosModelPage;
		
	}
	
	@GetMapping("/{codigoPedido}")
	public PedidoModel buscar(@PathVariable String codigoPedido) {
		Pedido pedido = emissaoPedido.buscarComTratamentoErro(codigoPedido);
		
		return pedidoModelAssembler.toModel(pedido);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PedidoModel emitir(@RequestBody @Valid PedidoInput pedidoInput) {
		
		try {
			Pedido novoPedido = pedidoInputDisassembler.toDomainObject(pedidoInput);
		
		novoPedido.setCliente(new Usuario());
		novoPedido.getCliente().setId(1L);
		
		return pedidoModelAssembler.toModel(emissaoPedido.emitir(novoPedido));
		
		}catch(EntidadeNaoEncontradaException e) {			
			throw new NegocioException(e.getMessage(), e.getCause());		
		}
		
	}
	
	private Pageable traduzirPageable(Pageable apiPageable) {
		
		var mapeamento = Map.of("codigo","codigo",
				"restaurante.nome", "restaurante.nome",
				"cliente.nome", "cliente.nome",
				"valorTotal", "valorTotal");
		
		return PageableTranslator.translate(apiPageable, mapeamento);	
		
	}
	
	

}
