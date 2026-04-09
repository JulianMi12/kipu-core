-- ==========================================
-- PREPARACIÓN DE SCHEMAS
-- ==========================================
CREATE SCHEMA IF NOT EXISTS identity;
CREATE SCHEMA IF NOT EXISTS contacts;

-- ==========================================
-- TIPOS ENUM (contacts)
-- ==========================================
CREATE TYPE contacts.event_recurrence_type AS ENUM ('ONCE', 'HOURLY', 'DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY');
CREATE TYPE contacts.event_status AS ENUM ('PENDING', 'COMPLETED');

-- ==========================================
-- TABLAS: identity
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
    user_id     UUID NOT NULL,
    token       VARCHAR(512) UNIQUE NOT NULL,
    expiry_date TIMESTAMPTZ NOT NULL,
    revoked     BOOLEAN DEFAULT FALSE
);

CREATE TABLE identity.user_kyc (
    user_id              UUID PRIMARY KEY,
    self_contact_id      UUID UNIQUE,
    status               VARCHAR(20) DEFAULT 'PENDING',
    onboarding_completed BOOLEAN DEFAULT FALSE,
    updated_at           TIMESTAMPTZ DEFAULT NOW()
);

-- ==========================================
-- TABLAS: contacts
-- ==========================================
CREATE TABLE contacts.user_tags (
    id            UUID PRIMARY KEY,
    owner_user_id UUID NOT NULL,
    name          VARCHAR(50) NOT NULL,
    color_hex     VARCHAR(7) DEFAULT '#0288d1'
);

CREATE TABLE contacts.contacts (
    id                 UUID PRIMARY KEY,
    owner_user_id      UUID NOT NULL,
    first_name         VARCHAR(100) NOT NULL,
    last_name          VARCHAR(100),
    primary_email      VARCHAR(255),
    birthdate          DATE,
    dynamic_attributes JSONB,
    created_at         TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE contacts.contact_tags (
    contact_id UUID NOT NULL,
    tag_id     UUID NOT NULL,
    PRIMARY KEY (contact_id, tag_id)
);

CREATE TABLE contacts.contact_events (
    id                   UUID PRIMARY KEY,
    contact_id           UUID NOT NULL,
    title                VARCHAR(255) NOT NULL,
    description          TEXT,
    start_date_time      TIMESTAMPTZ NOT NULL,
    alert_lead_time_days INT NOT NULL DEFAULT 0,
    recurrence_type      contacts.event_recurrence_type NOT NULL DEFAULT 'ONCE',
    recurrence_interval  INT NOT NULL DEFAULT 0,
    status               contacts.event_status NOT NULL DEFAULT 'PENDING',
    last_completed_date  TIMESTAMPTZ,
    timezone             VARCHAR(50) DEFAULT 'UTC',
    created_at           TIMESTAMPTZ DEFAULT NOW(),
    updated_at           TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE contacts.event_tags (
    event_id UUID NOT NULL,
    tag_id   UUID NOT NULL,
    PRIMARY KEY (event_id, tag_id)
);
