CREATE TABLE categorias_financeiras (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    fk_categoria_financeira_propriedade UUID NOT NULL,
    nome VARCHAR(120) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    ativo BOOLEAN NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,

    CONSTRAINT fk_categoria_financeira_propriedade FOREIGN KEY (fk_categoria_financeira_propriedade) REFERENCES propriedades_rurais (id)
);

CREATE INDEX idx_propriedade_categoria_financeira ON categorias_financeiras (fk_categoria_financeira_propriedade);