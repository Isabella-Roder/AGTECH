CREATE TABLE lancamentos_financeiros (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    fk_lancamento_financeiro_propriedade UUID NOT NULL,
    fk_lancamento_financeiro_categoria UUID NOT NULL,
    valor DECIMAL(14, 2) NOT NULL,
    data DATE NOT NULL,
    descricao VARCHAR(500),
    fk_lancamento_financeiro_safra UUID,
    ativo BOOLEAN NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,

    CONSTRAINT fk_lancamento_financeiro_propriedade FOREIGN KEY (fk_lancamento_financeiro_propriedade) REFERENCES propriedades_rurais (id),
    CONSTRAINT fk_lancamento_financeiro_categoria FOREIGN KEY (fk_lancamento_financeiro_categoria) REFERENCES categorias_financeiras (id),
    CONSTRAINT fk_lancamento_financeiro_safra FOREIGN KEY (fk_lancamento_financeiro_safra) REFERENCES safras (id)
);

CREATE INDEX idx_propriedade_lancamento_financeiro ON lancamentos_financeiros (fk_lancamento_financeiro_propriedade);
CREATE INDEX idx_categoria_lancamento_financeiro ON lancamentos_financeiros (fk_lancamento_financeiro_categoria);
CREATE INDEX idx_safra_lancamento_financeiro ON lancamentos_financeiros (fk_lancamento_financeiro_safra);