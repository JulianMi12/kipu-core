package com.kipu.core.contacts.infrastructure.persistence.jpa.entity;

import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import com.kipu.core.contacts.domain.model.enums.EventStatusEnum;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "contact_events", schema = "contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactEventJpaEntity {

  @Id
  @Column(nullable = false, updatable = false)
  private UUID id;

  @Column(name = "contact_id", nullable = false, updatable = false)
  private UUID contactId;

  @Column(name = "title", nullable = false, length = 255)
  private String title;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "base_date", nullable = false)
  private LocalDate baseDate;

  @Column(name = "alert_lead_time_days", nullable = false)
  private int alertLeadTimeDays;

  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(
      name = "recurrence_type",
      nullable = false,
      columnDefinition = "contacts.event_recurrence_type")
  private EventRecurrenceTypeEnum recurrenceType;

  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(name = "status", nullable = false, columnDefinition = "contacts.event_status")
  private EventStatusEnum status;

  @Column(name = "last_completed_date")
  private LocalDate lastCompletedDate;

  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(
      name = "event_tags",
      schema = "contacts",
      joinColumns = @JoinColumn(name = "event_id"))
  @Column(name = "tag_id")
  private Set<UUID> tagIds = new HashSet<>();

  public static ContactEventJpaEntity fromDomain(ContactEvent event) {
    return new ContactEventJpaEntity(
        event.getId(),
        event.getContactId(),
        event.getTitle(),
        event.getDescription(),
        event.getBaseDate(),
        event.getAlertLeadTimeDays(),
        event.getRecurrenceType(),
        event.getStatus(),
        event.getLastCompletedDate(),
        event.getCreatedAt(),
        event.getUpdatedAt(),
        event.getTagIds() != null ? new HashSet<>(event.getTagIds()) : new HashSet<>());
  }

  public ContactEvent toDomain() {
    return ContactEvent.reconstitute(
        id,
        contactId,
        title,
        description,
        baseDate,
        alertLeadTimeDays,
        recurrenceType,
        status,
        lastCompletedDate,
        this.tagIds != null ? this.tagIds : new HashSet<>(),
        createdAt,
        updatedAt);
  }
}
