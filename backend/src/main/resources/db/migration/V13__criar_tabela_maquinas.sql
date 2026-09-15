CREATE TABLE maquinas (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    fk_maquina_propriedade UUID NOT NULL,
    identificador VARCHAR(100) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    horimetro_atual DOUBLE PRECISION NOT NULL,
    ativo BOOLEAN NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,

    CONSTRAINT fk_maquina_propriedade FOREIGN KEY (fk_maquina_propriedade) REFERENCES propriedades_rurais (id)
);

CREATE INDEX idx_propriedade_maquina ON maquinas (fk_maquina_propriedade);