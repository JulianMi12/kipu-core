package com.kipu.core.contacts.infrastructure.persistence.jpa.repository;

import com.kipu.core.contacts.infrastructure.persistence.jpa.entity.ContactJpaEntity;
import com.kipu.core.contacts.infrastructure.persistence.jpa.projection.SearchResultProjection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaSearchRepository extends JpaRepository<ContactJpaEntity, UUID> {

  @Query(
      value =
          """
        SELECT
            c.id as id,
            'CONTACT' as type,
            (c.first_name || ' ' || c.last_name) as title,
            c.primary_email as subtitle,
            contacts.similarity((c.first_name || ' ' || c.last_name), :query) as score,
            (SELECT ct.tag_id FROM contacts.contact_tags ct WHERE ct.contact_id = c.id LIMIT 1) as tagId
        FROM contacts.contacts c
        WHERE c.owner_user_id = :ownerUserId
          AND ((c.first_name || ' ' || c.last_name) ILIKE (:query || '%')
               OR c.primary_email ILIKE (:query || '%')
               OR (c.first_name || ' ' || c.last_name) % :query)
        UNION ALL
        SELECT
            e.id as id,
            'EVENT' as type,
            e.title as title,
            to_char(e.start_date_time, 'DD/MM/YYYY HH24:MI') as subtitle,
            contacts.similarity(e.title, :query) as score,
            (SELECT et.tag_id FROM contacts.event_tags et WHERE et.event_id = e.id LIMIT 1) as tagId
        FROM contacts.contact_events e
        JOIN contacts.contacts c ON e.contact_id = c.id
        WHERE c.owner_user_id = :ownerUserId
          AND (e.title ILIKE (:query || '%') OR e.title % :query)
        ORDER BY score DESC
        LIMIT 15
        """,
      nativeQuery = true)
  List<SearchResultProjection> findGlobalSearchResults(
      @Param("ownerUserId") UUID ownerUserId, @Param("query") String query);
}
