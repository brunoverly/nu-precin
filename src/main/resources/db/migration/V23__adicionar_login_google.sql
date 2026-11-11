ALTER TABLE usuarios
    ADD COLUMN email_confirmado BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN google_sub VARCHAR(255),
    ADD CONSTRAINT uq_usuarios_google_sub UNIQUE (google_sub);
