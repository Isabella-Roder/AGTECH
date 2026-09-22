CREATE TABLE animais (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    fk_animal_rebanho UUID NOT NULL,
    identificacao VARCHAR(50) NOT NULL,
    sexo_animal VARCHAR(10) NOT NULL,
    data_nascimento DATE,
    ativo BOOLEAN NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,

    CONSTRAINT fk_animal_rebanho FOREIGN KEY (fk_animal_rebanho) REFERENCES rebanhos (id)
);

CREATE INDEX idx_rebanho_animal ON animais (fk_animal_rebanho);