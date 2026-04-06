package com.kipu.core.contacts.infrastructure.persistence.jpa.entity;

import com.kipu.core.contacts.domain.model.Contact;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

  public static ContactJpaEntity fromDomain(Contact contact) {
    return new ContactJpaEntity(
        contact.getId(),
        contact.getOwnerUserId(),
        contact.getFirstName(),
        contact.getLastName(),
        contact.getPrimaryEmail(),
        contact.getBirthdate(),
        contact.getDynamicAttributes(),
        contact.getCreatedAt());
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
        createdAt);
  }
}
