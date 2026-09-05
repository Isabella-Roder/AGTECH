CREATE TABLE propriedades_rurais (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    nome VARCHAR(80) NOT NULL,
    municipio VARCHAR(80) NOT NULL,
    estado VARCHAR(80) NOT NULL,
    area_total_hectares DOUBLE PRECISION NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL
)