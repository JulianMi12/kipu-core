-- =============================================================================
-- ENTITY: Refresh Tokens
-- DESCRIPTION: Persistencia para la rotación y revocación de sesiones.
-- =============================================================================

CREATE TABLE refresh_tokens (
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL,
    token       VARCHAR(512) UNIQUE NOT NULL,
    expiry_date TIMESTAMPTZ NOT NULL,
    revoked     BOOLEAN DEFAULT FALSE,

    -- Foreign Key con integridad referencial (Cascada al borrar usuario)
    CONSTRAINT fk_refresh_tokens_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE
);

-- Índice para optimizar búsquedas por usuario (Login/Logout/Refresh)
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);

-- Índice para búsquedas rápidas por el valor del token
CREATE INDEX idx_refresh_tokens_value ON refresh_tokens(token);