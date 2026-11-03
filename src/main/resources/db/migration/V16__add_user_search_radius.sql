ALTER TABLE usuarios
    ADD COLUMN raio_busca_km INTEGER NOT NULL DEFAULT 5;

ALTER TABLE usuarios
    ADD CONSTRAINT ck_usuarios_raio_busca_km CHECK (raio_busca_km IN (1, 3, 5, 10));
