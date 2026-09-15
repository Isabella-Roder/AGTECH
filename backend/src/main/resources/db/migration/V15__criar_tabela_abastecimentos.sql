CREATE TABLE abastecimentos (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    fk_abastecimento_maquina UUID NOT NULL,
    data TIMESTAMP NOT NULL,
    litros DOUBLE PRECISION NOT NULL,
    horimetro_no_momento DOUBLE PRECISION NOT NULL,
    observacoes VARCHAR(500),
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,

    CONSTRAINT fk_abastecimento_maquina FOREIGN KEY (fk_abastecimento_maquina) REFERENCES maquinas (id)
);

CREATE INDEX idx_abastecimento_maquina ON abastecimentos (fk_abastecimento_maquina);