CREATE TABLE movimentacoes_estoque(
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    fk_movimentacao_estoque_produto UUID NOT NULL,
    fk_movimentacao_estoque_deposito UUID NOT NULL,
    tipo VARCHAR(10) NOT NULL,
    quantidade DOUBLE PRECISION NOT NULL,
    data DATE NOT NULL,
    fk_movimentacao_estoque_safra UUID,
    observacoes VARCHAR(500),
    criado_em TIMESTAMP NOT NULL,

    CONSTRAINT fk_movimentacoes_produto FOREIGN KEY (fk_movimentacao_estoque_produto) REFERENCES produtos (id),
    CONSTRAINT fk_movimentacoes_deposito FOREIGN KEY (fk_movimentacao_estoque_deposito) REFERENCES depositos (id),
    CONSTRAINT fk_movimentacoes_safra FOREIGN KEY (fk_movimentacao_estoque_safra) REFERENCES safras (id)
);

CREATE INDEX idx_produto_movimentacao_estoque ON movimentacoes_estoque (fk_movimentacao_estoque_produto);
CREATE INDEX idx_deposito_movimentacao_estoque ON movimentacoes_estoque (fk_movimentacao_estoque_deposito);
CREATE INDEX idx_safra_movimentacao_estoque ON movimentacoes_estoque (fk_movimentacao_estoque_safra);