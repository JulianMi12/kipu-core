package com.kipu.core.contacts.infrastructure.persistence.jpa.entity;

import com.kipu.core.contacts.domain.model.Contact;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "contacts", schema = "contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactJpaEntity {

  @Id
  @Column(nullable = false, updatable = false)
  private UUID id;

  @Column(name = "owner_user_id", nullable = false, updatable = false)
  private UUID ownerUserId;

  @Column(name = "first_name", nullable = false, length = 100)
  private String firstName;

  @Column(name = "last_name", length = 100)
  private String lastName;

  @Column(name = "primary_email", length = 255)
  private String primaryEmail;

  @Column(name = "birthdate")
  private LocalDate birthdate;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "dynamic_attributes", columnDefinition = "jsonb")
  private Map<String, Object> dynamicAttributes;

  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(
      name = "contact_tags",
      schema = "contacts",
      joinColumns = @JoinColumn(name = "contact_id"))
  @Column(name = "tag_id")
  @BatchSize(size = 20)
  private Set<UUID> tagIds = new HashSet<>();

  public static ContactJpaEntity fromDomain(Contact contact) {
    return new ContactJpaEntity(
        contact.getId(),
        contact.getOwnerUserId(),
        contact.getFirstName(),
        contact.getLastName(),
        contact.getPrimaryEmail(),
        contact.getBirthdate(),
        contact.getDynamicAttributes(),
        contact.getCreatedAt(),
        contact.getTagIds() != null ? new HashSet<>(contact.getTagIds()) : new HashSet<>());
  }

  public Contact toDomain() {
    return Contact.reconstitute(
        id,
        ownerUserId,
        firstName,
        lastName,
        primaryEmail,
        birthdate,
        dynamicAttributes,
        tagIds != null ? tagIds : new HashSet<>(),
        createdAt);
  }
}
