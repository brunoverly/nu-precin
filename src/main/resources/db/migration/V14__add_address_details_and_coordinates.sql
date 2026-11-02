ALTER TABLE enderecos
    ADD COLUMN numero VARCHAR(255),
    ADD COLUMN cep VARCHAR(8),
    ADD COLUMN latitude DOUBLE PRECISION,
    ADD COLUMN longitude DOUBLE PRECISION;

ALTER TABLE enderecos
    ADD CONSTRAINT ck_enderecos_coordinates CHECK (
        (latitude IS NULL AND longitude IS NULL)
        OR (latitude IS NOT NULL AND longitude IS NOT NULL
            AND latitude BETWEEN -90 AND 90
            AND longitude BETWEEN -180 AND 180)
    );
