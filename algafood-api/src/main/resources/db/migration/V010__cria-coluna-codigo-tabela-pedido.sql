alter table pedido add column codigo varchar(36) not null after id;
update pedido set codigo = UUID();
alter table pedido add constraint uk_pedido_codigo unique (codigo);