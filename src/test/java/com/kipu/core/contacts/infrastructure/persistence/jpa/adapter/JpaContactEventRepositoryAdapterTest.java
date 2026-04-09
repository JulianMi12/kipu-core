package com.kipu.core.contacts.infrastructure.persistence.jpa.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import com.kipu.core.contacts.domain.model.enums.EventStatusEnum;
import com.kipu.core.contacts.infrastructure.persistence.jpa.entity.ContactEventJpaEntity;
import com.kipu.core.contacts.infrastructure.persistence.jpa.repository.JpaContactEventRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaContactEventRepositoryAdapterTest {

  @Mock private JpaContactEventRepository jpaContactEventRepository;

  @InjectMocks private JpaContactEventRepositoryAdapter jpaContactEventRepositoryAdapter;

  @Test
  @DisplayName("save: Should map to entity, save, and map back to domain")
  void save_ShouldReturnSavedContactEvent_WhenDataIsValid() {
    // Arrange
    // Corregido: Firma de 9 argumentos y OffsetDateTime
    ContactEvent event =
        ContactEvent.create(
            UUID.randomUUID(),
            "Test Event",
            "Description",
            OffsetDateTime.now(),
            5,
            EventRecurrenceTypeEnum.ONCE,
            1,
            "UTC",
            Set.of());

    ContactEventJpaEntity entity = ContactEventJpaEntity.fromDomain(event);

    when(jpaContactEventRepository.save(any(ContactEventJpaEntity.class))).thenReturn(entity);

    // Act
    ContactEvent result = jpaContactEventRepositoryAdapter.save(event);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(event.getId());
    verify(jpaContactEventRepository).save(any(ContactEventJpaEntity.class));
  }

  @Test
  @DisplayName("findById: Should return domain event when entity exists")
  void findById_ShouldReturnOptionalWithEvent_WhenEventExists() {
    // Arrange
    UUID eventId = UUID.randomUUID();
    ContactEventJpaEntity entity = createMockEntity(eventId);

    when(jpaContactEventRepository.findById(eventId)).thenReturn(Optional.of(entity));

    // Act
    Optional<ContactEvent> result = jpaContactEventRepositoryAdapter.findById(eventId);

    // Assert
    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(eventId);
    verify(jpaContactEventRepository).findById(eventId);
  }

  @Test
  @DisplayName("findByIdWithTags: Should call the specific repository method with JOIN FETCH")
  void findByIdWithTags_ShouldReturnEvent_WhenExists() {
    // Arrange
    UUID eventId = UUID.randomUUID();
    UUID tagId = UUID.randomUUID();
    ContactEventJpaEntity entity = createMockEntity(eventId);
    entity.setTagIds(Set.of(tagId));

    when(jpaContactEventRepository.findByIdWithTags(eventId)).thenReturn(Optional.of(entity));

    // Act
    Optional<ContactEvent> result = jpaContactEventRepositoryAdapter.findByIdWithTags(eventId);

    // Assert
    assertThat(result).isPresent();
    assertThat(result.get().getTagIds()).contains(tagId);
    verify(jpaContactEventRepository).findByIdWithTags(eventId);
  }

  @Test
  @DisplayName("findByIdWithTags: Should return empty when event does not exist")
  void findByIdWithTags_ShouldReturnEmpty_WhenNotFound() {
    // Arrange
    UUID eventId = UUID.randomUUID();
    when(jpaContactEventRepository.findByIdWithTags(eventId)).thenReturn(Optional.empty());

    // Act
    Optional<ContactEvent> result = jpaContactEventRepositoryAdapter.findByIdWithTags(eventId);

    // Assert
    assertThat(result).isEmpty();
    verify(jpaContactEventRepository).findByIdWithTags(eventId);
  }

  @Test
  @DisplayName("delete: Should call deleteById in the JPA repository")
  void delete_ShouldInvokeRepositoryDelete_WhenIdIsProvided() {
    // Arrange
    UUID eventId = UUID.randomUUID();

    // Act
    jpaContactEventRepositoryAdapter.delete(eventId);

    // Assert
    verify(jpaContactEventRepository).deleteById(eventId);
  }

  /** Helper para crear una entidad JPA válida que no rompa el mapeo toDomain() */
  private ContactEventJpaEntity createMockEntity(UUID id) {
    ContactEventJpaEntity entity = new ContactEventJpaEntity();
    entity.setId(id);
    entity.setContactId(UUID.randomUUID());
    entity.setTitle("Title");
    entity.setDescription("Description");
    entity.setStartDateTime(OffsetDateTime.now());
    entity.setAlertLeadTimeDays(0);
    entity.setRecurrenceType(EventRecurrenceTypeEnum.ONCE);
    entity.setRecurrenceInterval(1);
    entity.setStatus(EventStatusEnum.PENDING);
    entity.setTimezone("UTC");
    entity.setTagIds(Set.of());
    entity.setCreatedAt(OffsetDateTime.now());
    entity.setUpdatedAt(OffsetDateTime.now());
    return entity;
  }

  @Test
  @DisplayName(
      "findByContactIdAndTagIdsContains: Should return event when contactId and tagId match")
  void findByContactIdAndTagIdsContains_ShouldReturnEvent_WhenMatches() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    UUID tagId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();

    ContactEventJpaEntity entity = createMockEntity(eventId);
    entity.setContactId(contactId);
    entity.setTagIds(Set.of(tagId));

    when(jpaContactEventRepository.findByContactIdAndTagId(contactId, tagId))
        .thenReturn(Optional.of(entity));

    // Act
    Optional<ContactEvent> result =
        jpaContactEventRepositoryAdapter.findByContactIdAndTagIdsContains(contactId, tagId);

    // Assert
    assertThat(result).isPresent();
    assertThat(result.get().getContactId()).isEqualTo(contactId);
    assertThat(result.get().getTagIds()).contains(tagId);
    verify(jpaContactEventRepository).findByContactIdAndTagId(contactId, tagId);
  }

  @Test
  @DisplayName("findByContactIdAndTagIdsContains: Should return empty when no match found")
  void findByContactIdAndTagIdsContains_ShouldReturnEmpty_WhenNoMatch() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    UUID tagId = UUID.randomUUID();

    when(jpaContactEventRepository.findByContactIdAndTagId(contactId, tagId))
        .thenReturn(Optional.empty());

    // Act
    Optional<ContactEvent> result =
        jpaContactEventRepositoryAdapter.findByContactIdAndTagIdsContains(contactId, tagId);

    // Assert
    assertThat(result).isEmpty();
    verify(jpaContactEventRepository).findByContactIdAndTagId(contactId, tagId);
  }
}
