CREATE TABLE manutencoes (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    fk_manutencao_maquina UUID NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    data_realizacao DATE NOT NULL,
    horimetro_no_momento DOUBLE PRECISION NOT NULL,
    descricao VARCHAR(500),
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,

    CONSTRAINT fk_manutencao_maquina FOREIGN KEY (fk_manutencao_maquina) REFERENCES maquinas (id)
);

CREATE INDEX idx_manutencao_maquina ON manutencoes (fk_manutencao_maquina);