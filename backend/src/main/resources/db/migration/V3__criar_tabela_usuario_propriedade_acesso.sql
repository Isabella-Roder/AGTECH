CREATE TABLE usuario_propriedade_acesso(
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    usuario_id UUID NOT NULL,
    propriedade_id UUID NOT NULL,
    papel VARCHAR(20) NOT NULL,
    CONSTRAINT fk_upa_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
    CONSTRAINT fk_upa_propriedade FOREIGN KEY (propriedade_id) REFERENCES propriedades_rurais (id),
    CONSTRAINT uk_upa_usuario_propriedade UNIQUE (usuario_id, propriedade_id)
)