-- ==========================================
-- SCHEMA: contacts (Linking Events to Tags & Integrity)
-- ==========================================

-- 1. Crear tabla intermedia para reutilizar user_tags en eventos
CREATE TABLE IF NOT EXISTS contacts.event_tags (
    event_id UUID NOT NULL REFERENCES contacts.contact_events(id) ON DELETE CASCADE,
    tag_id   UUID NOT NULL REFERENCES contacts.user_tags(id) ON DELETE CASCADE,
    PRIMARY KEY (event_id, tag_id)
);

-- 2. Índice para filtrado rápido de eventos por etiqueta
CREATE INDEX IF NOT EXISTS idx_event_tags_tag_id ON contacts.event_tags(tag_id);

-- 3. Restricción de unicidad: Un usuario no puede tener dos tags con el mismo nombre
-- NOTA: Asegúrate de que no existan duplicados antes de ejecutar esto en un entorno con datos.
ALTER TABLE contacts.user_tags
    ADD CONSTRAINT uk_user_tags_owner_name UNIQUE (owner_user_id, name);