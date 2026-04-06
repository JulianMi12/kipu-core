package com.kipu.core.contacts.infrastructure.persistence.jpa.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.infrastructure.persistence.jpa.entity.ContactJpaEntity;
import com.kipu.core.contacts.infrastructure.persistence.jpa.repository.JpaContactRepository;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
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

    // Mockeamos el modelo de dominio para el mapeo
    Contact contact = mock(Contact.class);
    when(contact.getId()).thenReturn(contactId);
    when(contact.getOwnerUserId()).thenReturn(ownerId);
    when(contact.getFirstName()).thenReturn("Julian");
    when(contact.getLastName()).thenReturn("Miranda");
    when(contact.getPrimaryEmail()).thenReturn("dev@kipu.com");
    when(contact.getBirthdate()).thenReturn(birthdate);
    when(contact.getDynamicAttributes()).thenReturn(attributes);
    when(contact.getCreatedAt()).thenReturn(now);

    // Act
    jpaContactRepositoryAdapter.save(contact);

    // Assert & Verify
    ArgumentCaptor<ContactJpaEntity> entityCaptor = ArgumentCaptor.forClass(ContactJpaEntity.class);
    verify(jpaContactRepository).save(entityCaptor.capture());

    ContactJpaEntity capturedEntity = entityCaptor.getValue();

    // Validamos que el adaptador haya realizado la conversión correctamente antes de llamar al
    // JpaRepository
    assertEquals(contactId, capturedEntity.getId());
    assertEquals(ownerId, capturedEntity.getOwnerUserId());
    assertEquals("Julian", capturedEntity.getFirstName());
    assertEquals("Miranda", capturedEntity.getLastName());
    assertEquals("dev@kipu.com", capturedEntity.getPrimaryEmail());
    assertEquals(birthdate, capturedEntity.getBirthdate());
    assertEquals(attributes, capturedEntity.getDynamicAttributes());
    assertEquals(now, capturedEntity.getCreatedAt());
  }

  @Test
  @DisplayName("findById: Should return domain contact when entity exists in DB")
  void findById_WhenExists_ShouldReturnDomainContact() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();

    // Creamos la entidad JPA que simula estar en la BD
    ContactJpaEntity jpaEntity = new ContactJpaEntity();
    jpaEntity.setId(contactId);
    jpaEntity.setOwnerUserId(ownerId);
    jpaEntity.setFirstName("Julian");
    jpaEntity.setLastName("Miranda");
    jpaEntity.setPrimaryEmail("dev@kipu.com");
    jpaEntity.setDynamicAttributes(Map.of());

    when(jpaContactRepository.findById(contactId)).thenReturn(Optional.of(jpaEntity));

    // Act
    Optional<Contact> result = jpaContactRepositoryAdapter.findById(contactId);

    // Assert
    assertThat(result).isPresent();
    assertEquals(contactId, result.get().getId());
    assertEquals(ownerId, result.get().getOwnerUserId());
    assertEquals("Julian", result.get().getFirstName());
    assertEquals("Miranda", result.get().getLastName());

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
    verify(jpaContactRepository).findById(contactId);
  }
}
