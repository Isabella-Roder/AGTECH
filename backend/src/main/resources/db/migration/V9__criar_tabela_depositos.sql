CREATE TABLE depositos (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    fk_deposito_propriedade UUID NOT NULL,
    nome VARCHAR(100) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,

    CONSTRAINT fk_deposito FOREIGN KEY (fk_deposito_propriedade) REFERENCES propriedades_rurais (id)
);

CREATE INDEX idx_propriedade_deposito ON depositos (fk_deposito_propriedade);