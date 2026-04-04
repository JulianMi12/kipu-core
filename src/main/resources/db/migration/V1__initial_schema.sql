-- Creación de Schemas
CREATE SCHEMA IF NOT EXISTS identity;
CREATE SCHEMA IF NOT EXISTS contacts;

-- ==========================================
-- SCHEMA: identity
-- ==========================================

CREATE TABLE identity.users (
    id            UUID PRIMARY KEY,
    email         VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    is_active     BOOLEAN DEFAULT TRUE,
    created_at    TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE identity.refresh_tokens (
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL REFERENCES identity.users(id) ON DELETE CASCADE,
    token       VARCHAR(512) UNIQUE NOT NULL,
    expiry_date TIMESTAMPTZ NOT NULL,
    revoked     BOOLEAN DEFAULT FALSE
);

-- ==========================================
-- SCHEMA: contacts
-- ==========================================

CREATE TABLE contacts.user_tags (
    id            UUID PRIMARY KEY,
    owner_user_id UUID NOT NULL REFERENCES identity.users(id) ON DELETE CASCADE,
    name          VARCHAR(50) NOT NULL,
    color_hex     VARCHAR(7) DEFAULT '#0288d1'
);

CREATE TABLE contacts.contacts (
    id                 UUID PRIMARY KEY,
    owner_user_id      UUID NOT NULL REFERENCES identity.users(id) ON DELETE CASCADE,
    first_name         VARCHAR(100) NOT NULL,
    last_name          VARCHAR(100),
    primary_email      VARCHAR(255),
    dynamic_attributes JSONB,
    created_at         TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE contacts.contact_tags (
    contact_id UUID NOT NULL REFERENCES contacts.contacts(id) ON DELETE CASCADE,
    tag_id     UUID NOT NULL REFERENCES contacts.user_tags(id) ON DELETE CASCADE,
    PRIMARY KEY (contact_id, tag_id)
);

-- ==========================================
-- SCHEMA: identity (Ampliación para Onboarding)
-- ==========================================

CREATE TABLE identity.user_kyc (
    user_id              UUID PRIMARY KEY REFERENCES identity.users(id) ON DELETE CASCADE,
    self_contact_id      UUID UNIQUE REFERENCES contacts.contacts(id) ON DELETE SET NULL,
    status               VARCHAR(20) DEFAULT 'PENDING',
    onboarding_completed BOOLEAN DEFAULT FALSE,
    birthdate            DATE,
    updated_at           TIMESTAMPTZ DEFAULT NOW()
);

-- ==========================================
-- ÍNDICES PARA RENDIMIENTO
-- ==========================================
CREATE INDEX idx_users_email ON identity.users(email);
CREATE INDEX idx_refresh_tokens_user ON identity.refresh_tokens(user_id);
CREATE INDEX idx_contacts_owner ON contacts.contacts(owner_user_id);
CREATE INDEX idx_tags_owner ON contacts.user_tags(owner_user_id);