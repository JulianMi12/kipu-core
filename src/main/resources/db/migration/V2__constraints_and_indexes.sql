-- ==========================================
-- CONSTRAINTS (FOREIGN KEYS)
-- ==========================================

-- identity
ALTER TABLE identity.refresh_tokens
    ADD CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES identity.users(id) ON DELETE CASCADE;

ALTER TABLE identity.user_kyc
    ADD CONSTRAINT fk_user_kyc_user FOREIGN KEY (user_id) REFERENCES identity.users(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_user_kyc_self_contact FOREIGN KEY (self_contact_id) REFERENCES contacts.contacts(id) ON DELETE SET NULL;

-- contacts
ALTER TABLE contacts.user_tags
    ADD CONSTRAINT fk_user_tags_owner FOREIGN KEY (owner_user_id) REFERENCES identity.users(id) ON DELETE CASCADE;

ALTER TABLE contacts.contacts
    ADD CONSTRAINT fk_contacts_owner FOREIGN KEY (owner_user_id) REFERENCES identity.users(id) ON DELETE CASCADE;

ALTER TABLE contacts.contact_tags
    ADD CONSTRAINT fk_ct_contact FOREIGN KEY (contact_id) REFERENCES contacts.contacts(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_ct_tag FOREIGN KEY (tag_id) REFERENCES contacts.user_tags(id) ON DELETE CASCADE;

ALTER TABLE contacts.contact_events
    ADD CONSTRAINT fk_events_contact FOREIGN KEY (contact_id) REFERENCES contacts.contacts(id) ON DELETE CASCADE;

ALTER TABLE contacts.event_tags
    ADD CONSTRAINT fk_et_event FOREIGN KEY (event_id) REFERENCES contacts.contact_events(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_et_tag FOREIGN KEY (tag_id) REFERENCES contacts.user_tags(id) ON DELETE CASCADE;

-- ==========================================
-- UNIQUE CONSTRAINTS
-- ==========================================

-- Un usuario no puede tener dos etiquetas con el mismo nombre
ALTER TABLE contacts.user_tags
    ADD CONSTRAINT uk_user_tags_owner_name UNIQUE (owner_user_id, name);

-- Un dueño no puede tener dos contactos con el mismo email (parcial para permitir nulos)
CREATE UNIQUE INDEX uk_contacts_owner_email
    ON contacts.contacts (owner_user_id, primary_email)
    WHERE primary_email IS NOT NULL;

-- ==========================================
-- ÍNDICES DE RENDIMIENTO
-- ==========================================

-- Búsquedas generales
CREATE INDEX idx_users_email ON identity.users(email);
CREATE INDEX idx_refresh_tokens_user ON identity.refresh_tokens(user_id);
CREATE INDEX idx_contacts_owner ON contacts.contacts(owner_user_id);
CREATE INDEX idx_tags_owner ON contacts.user_tags(owner_user_id);

-- Paginación de contactos (Listado principal)
CREATE INDEX idx_contacts_pagination ON contacts.contacts (owner_user_id, created_at DESC);

-- Motor de eventos y agenda
CREATE INDEX idx_contact_events_contact_id ON contacts.contact_events(contact_id);
CREATE INDEX idx_contact_events_start_date ON contacts.contact_events(start_date_time);
CREATE INDEX idx_contact_events_status     ON contacts.contact_events(status);
CREATE INDEX idx_event_tags_tag_id         ON contacts.event_tags(tag_id);