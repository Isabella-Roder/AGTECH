CREATE TABLE safras (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    talhao_id UUID NOT NULL,
    cultura_id UUID NOT NULL,
    nome VARCHAR(70) NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim_prevista DATE NOT NULL,
    data_fim_real DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANEJADA',

    CONSTRAINT fk_safras_talhao FOREIGN KEY (talhao_id) REFERENCES talhoes (id),
    CONSTRAINT fk_safras_cultura FOREIGN KEY (cultura_id) REFERENCES culturas (id)
);

CREATE INDEX idx_talhao_safra ON safras (talhao_id);
CREATE INDEX idx_cultura_safra ON safras (cultura_id);