CREATE TABLE rebanhos (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    fk_rebanho_propriedade UUID NOT NULL,
    nome VARCHAR(100) NOT NULL,
    especie VARCHAR(20) NOT NULL,
    ativo BOOLEAN NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,

    CONSTRAINT fk_rebanho_propriedade FOREIGN KEY (fk_rebanho_propriedade) REFERENCES propriedades_rurais (id)
);

CREATE INDEX idx_propriedade_rebanho ON rebanhos (fk_rebanho_propriedade);