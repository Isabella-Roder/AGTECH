CREATE TABLE colheitas (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    fk_colheita_safra UUID NOT NULL,
    data_colheita DATE NOT NULL,
    quantidade_colhida DOUBLE PRECISION NOT NULL,
    unidade_medida VARCHAR(20) NOT NULL,
    observacoes VARCHAR(500),
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,

    CONSTRAINT fk_colheitas_safra FOREIGN KEY (fk_colheita_safra) REFERENCES safras (id)
);

CREATE INDEX idx_safra_colheita ON colheitas (fk_colheita_safra);