CREATE TABLE talhoes (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    propriedade_id UUID NOT NULL,
    nome VARCHAR(80) NOT NULL,
    area_hectares DOUBLE PRECISION NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,
    CONSTRAINT fk_talhoes_propriedade FOREIGN KEY (propriedade_id) REFERENCES propriedades_rurais (id)
);