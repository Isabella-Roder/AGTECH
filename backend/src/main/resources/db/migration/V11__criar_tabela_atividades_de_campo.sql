CREATE TABLE atividades_de_campo (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    fk_atividade_campo_safra UUID NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    data_realizacao DATE NOT NULL,
    observacoes VARCHAR(500),
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,

    CONSTRAINT fk_atividades_safra FOREIGN KEY (fk_atividade_campo_safra) REFERENCES safras (id)
);

CREATE INDEX idx_safra_atividade_campo ON atividades_de_campo (fk_atividade_campo_safra);