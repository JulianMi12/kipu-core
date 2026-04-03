-- ==========================================
-- DOMINIO DE IDENTIDAD (IAM)
-- ==========================================
CREATE TABLE users (
    id            UUID PRIMARY KEY,
    email         VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    is_active     BOOLEAN DEFAULT TRUE,
    created_at    TIMESTAMPTZ DEFAULT NOW()
);

-- ==========================================
-- DOMINIO DE CONTACTOS (DIRECTORY)
-- ==========================================
CREATE TABLE user_tags (
    id            UUID PRIMARY KEY,
    owner_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name          VARCHAR(50) NOT NULL,
    color_hex     VARCHAR(7) DEFAULT '#0288d1'
);

CREATE TABLE contacts (
    id                 UUID PRIMARY KEY,
    owner_user_id      UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    is_self            BOOLEAN NOT NULL DEFAULT FALSE,
    first_name         VARCHAR(100) NOT NULL,
    last_name          VARCHAR(100),
    primary_email      VARCHAR(255),
    dynamic_attributes JSONB,
    created_at         TIMESTAMPTZ DEFAULT NOW()
);

-- RELACIÓN MUCHOS A MUCHOS: CONTACTOS <-> ETIQUETAS
CREATE TABLE contact_tags (
    contact_id UUID NOT NULL REFERENCES contacts(id) ON DELETE CASCADE,
    tag_id     UUID NOT NULL REFERENCES user_tags(id) ON DELETE CASCADE,
    PRIMARY KEY (contact_id, tag_id)
);

-- ÍNDICES PARA RENDIMIENTO
CREATE INDEX idx_contacts_owner ON contacts(owner_user_id);
CREATE INDEX idx_tags_owner ON user_tags(owner_user_id);
CREATE INDEX idx_contacts_tags_tag ON contact_tags(tag_id);