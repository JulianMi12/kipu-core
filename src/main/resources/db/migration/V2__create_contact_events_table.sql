-- ==========================================
-- SCHEMA: contacts (Motor de Eventos y Alertas)
-- ==========================================

-- 1. Creación de Tipos ENUM para estados y recurrencia
CREATE TYPE contacts.event_recurrence_type AS ENUM ('ONCE', 'DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY');
CREATE TYPE contacts.event_status AS ENUM ('PENDING', 'COMPLETED');

-- 2. Tabla principal de Eventos vinculados a Entidades/Contactos
CREATE TABLE contacts.contact_events (
    id                   UUID PRIMARY KEY,
    contact_id           UUID NOT NULL REFERENCES contacts.contacts(id) ON DELETE CASCADE,
    title                VARCHAR(255) NOT NULL,
    description          TEXT,
    base_date            DATE NOT NULL,
    alert_lead_time_days INT NOT NULL DEFAULT 0,
    recurrence_type      contacts.event_recurrence_type NOT NULL DEFAULT 'ONCE',
    status               contacts.event_status NOT NULL DEFAULT 'PENDING',
    last_completed_date  DATE,
    created_at           TIMESTAMPTZ DEFAULT NOW(),
    updated_at           TIMESTAMPTZ DEFAULT NOW()
);

-- ==========================================
-- ÍNDICES PARA RENDIMIENTO
-- ==========================================

-- Índices cruciales para la carga rápida de las bandejas "Your Day" y "Upcoming"
CREATE INDEX idx_contact_events_contact_id ON contacts.contact_events(contact_id);
CREATE INDEX idx_contact_events_base_date  ON contacts.contact_events(base_date);
CREATE INDEX idx_contact_events_status     ON contacts.contact_events(status);