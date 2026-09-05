CREATE TABLE culturas (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    nome VARCHAR(60) NOT NULL,
    CONSTRAINT uk_culturas_nome UNIQUE (nome)
);