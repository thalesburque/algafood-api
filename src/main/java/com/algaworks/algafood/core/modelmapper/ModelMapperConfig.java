package com.algaworks.algafood.core.modelmapper;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.algaworks.algafood.api.model.EnderecoModel;
import com.algaworks.algafood.api.model.input.ItemPedidoInput;
import com.algaworks.algafood.domain.model.Endereco;
import com.algaworks.algafood.domain.model.ItemPedido;

@Configuration
public class ModelMapperConfig {

	@Bean
	public ModelMapper modelMapper() {

		var modelMapper = new ModelMapper();
		
		modelMapper.typeMap(ItemPedidoInput.class, ItemPedido.class).addMappings(mp -> mp.skip(ItemPedido::setId));

		var enderecoToEnderecoModelTypeMap = modelMapper.createTypeMap(Endereco.class, EnderecoModel.class);

		enderecoToEnderecoModelTypeMap.<String>addMapping(src -> src.getCidade().getEstado().getNome(),
				(dest, value) -> dest.getCidade().setEstado(value));

		return modelMapper;
	}

}
