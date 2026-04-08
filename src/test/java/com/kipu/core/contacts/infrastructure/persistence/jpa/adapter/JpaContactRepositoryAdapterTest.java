package com.kipu.core.contacts.infrastructure.persistence.jpa.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.infrastructure.persistence.jpa.entity.ContactJpaEntity;
import com.kipu.core.contacts.infrastructure.persistence.jpa.repository.JpaContactRepository;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaContactRepositoryAdapterTest {

  @Mock private JpaContactRepository jpaContactRepository;

  @InjectMocks private JpaContactRepositoryAdapter jpaContactRepositoryAdapter;

  @Test
  @DisplayName("save: Should convert domain contact to JPA entity and persist it")
  void save_ShouldConvertAndPersist() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();
    LocalDate birthdate = LocalDate.of(1990, 1, 1);
    Map<String, Object> attributes = Map.of("key", "value");

    Contact contact = mock(Contact.class);
    when(contact.getId()).thenReturn(contactId);
    when(contact.getOwnerUserId()).thenReturn(ownerId);
    when(contact.getFirstName()).thenReturn("Julian");
    when(contact.getLastName()).thenReturn("Miranda");
    when(contact.getPrimaryEmail()).thenReturn("dev@kipu.com");
    when(contact.getBirthdate()).thenReturn(birthdate);
    when(contact.getDynamicAttributes()).thenReturn(attributes);
    when(contact.getCreatedAt()).thenReturn(now);
    when(contact.getTagIds()).thenReturn(Set.of());

    // Act
    jpaContactRepositoryAdapter.save(contact);

    // Assert & Verify
    ArgumentCaptor<ContactJpaEntity> entityCaptor = ArgumentCaptor.forClass(ContactJpaEntity.class);
    verify(jpaContactRepository).save(entityCaptor.capture());

    ContactJpaEntity capturedEntity = entityCaptor.getValue();
    assertEquals(contactId, capturedEntity.getId());
    assertEquals(ownerId, capturedEntity.getOwnerUserId());
    verify(jpaContactRepository).save(any(ContactJpaEntity.class));
  }

  @Test
  @DisplayName("findById: Should return domain contact when entity exists in DB")
  void findById_WhenExists_ShouldReturnDomainContact() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    ContactJpaEntity jpaEntity = createMockEntity(contactId);

    when(jpaContactRepository.findById(contactId)).thenReturn(Optional.of(jpaEntity));

    // Act
    Optional<Contact> result = jpaContactRepositoryAdapter.findById(contactId);

    // Assert
    assertThat(result).isPresent();
    assertEquals(contactId, result.get().getId());
    verify(jpaContactRepository).findById(contactId);
  }

  @Test
  @DisplayName("findById: Should return empty Optional when entity does not exist")
  void findById_WhenNotExists_ShouldReturnEmpty() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    when(jpaContactRepository.findById(contactId)).thenReturn(Optional.empty());

    // Act
    Optional<Contact> result = jpaContactRepositoryAdapter.findById(contactId);

    // Assert
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("findByIdWithTags: Should return domain contact with tags loaded")
  void findByIdWithTags_WhenExists_ShouldReturnContactWithTags() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    UUID tagId = UUID.randomUUID();
    ContactJpaEntity jpaEntity = createMockEntity(contactId);
    jpaEntity.setTagIds(new HashSet<>(Set.of(tagId)));

    when(jpaContactRepository.findByIdWithTags(contactId)).thenReturn(Optional.of(jpaEntity));

    // Act
    Optional<Contact> result = jpaContactRepositoryAdapter.findByIdWithTags(contactId);

    // Assert
    assertThat(result).isPresent();
    assertThat(result.get().getTagIds()).contains(tagId);
    verify(jpaContactRepository).findByIdWithTags(contactId);
  }

  @Test
  @DisplayName("findByIdWithTags: Should return empty when entity does not exist")
  void findByIdWithTags_WhenNotExists_ShouldReturnEmpty() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    when(jpaContactRepository.findByIdWithTags(contactId)).thenReturn(Optional.empty());

    // Act
    Optional<Contact> result = jpaContactRepositoryAdapter.findByIdWithTags(contactId);

    // Assert
    assertThat(result).isEmpty();
    verify(jpaContactRepository).findByIdWithTags(contactId);
  }

  private ContactJpaEntity createMockEntity(UUID id) {
    ContactJpaEntity entity = new ContactJpaEntity();
    entity.setId(id);
    entity.setOwnerUserId(UUID.randomUUID());
    entity.setFirstName("Julian");
    entity.setLastName("Miranda");
    entity.setPrimaryEmail("dev@kipu.com");
    entity.setDynamicAttributes(Map.of());
    entity.setCreatedAt(OffsetDateTime.now());
    entity.setTagIds(new HashSet<>());
    return entity;
  }
}
