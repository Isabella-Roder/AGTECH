CREATE TABLE plantios(
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    safra_id UUID NOT NULL,
    data_plantio DATE NOT NULL,
    area_plantada_hectares DOUBLE PRECISION NOT NULL,
    observacoes VARCHAR(500),

    CONSTRAINT fk_plantios_safra FOREIGN KEY (safra_id) REFERENCES safras (id)
);

CREATE INDEX idx_safra_plantio ON plantios (safra_id);