alter table algafood.forma_pagamento add data_atualizacao datetime null;
update algafood.forma_pagamento set data_atualizacao = utc_timestamp;
alter table algafood.forma_pagamento modify data_atualizacao datetime not null;