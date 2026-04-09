-- ==========================================
-- MIGRATION: Optimization & Integrity for Contacts
-- ==========================================

-- 1. Índice de alto rendimiento para el listado de agenda
-- Este índice permite que la consulta paginada filtrada por dueño
-- y ordenada por fecha de creación sea instantánea (Index Scan),
-- evitando que PostgreSQL tenga que ordenar en memoria.
CREATE INDEX IF NOT EXISTS idx_contacts_pagination
    ON contacts.contacts (owner_user_id, created_at DESC);

-- 2. Restricción de unicidad para el correo principal por dueño
-- El mismo dueño no puede tener dos contactos con el mismo email.
-- Se utiliza un índice parcial (WHERE IS NOT NULL) para permitir
-- que múltiples contactos tengan el campo email vacío sin conflictos.
CREATE UNIQUE INDEX IF NOT EXISTS uk_contacts_owner_email
    ON contacts.contacts (owner_user_id, primary_email)
    WHERE primary_email IS NOT NULL;