package com.kipu.core.contacts.infrastructure.persistence.jpa.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import com.kipu.core.contacts.infrastructure.persistence.jpa.entity.ContactEventJpaEntity;
import com.kipu.core.contacts.infrastructure.persistence.jpa.repository.JpaContactEventRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
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
  void save_ShouldReturnSavedContactEvent_WhenDataIsValid() {
    // Arrange
    ContactEvent event =
        ContactEvent.create(
            UUID.randomUUID(),
            "Test Event",
            "Description",
            LocalDate.now(),
            5,
            EventRecurrenceTypeEnum.ONCE);
    ContactEventJpaEntity entity = ContactEventJpaEntity.fromDomain(event);

    when(jpaContactEventRepository.save(any(ContactEventJpaEntity.class))).thenReturn(entity);

    // Act
    ContactEvent result = jpaContactEventRepositoryAdapter.save(event);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(event.getId());
    assertThat(result.getTitle()).isEqualTo(event.getTitle());
    verify(jpaContactEventRepository).save(any(ContactEventJpaEntity.class));
  }

  @Test
  void findById_ShouldReturnOptionalWithEvent_WhenEventExists() {
    // Arrange
    UUID eventId = UUID.randomUUID();
    ContactEvent event =
        ContactEvent.create(
            UUID.randomUUID(), "Title", "Desc", LocalDate.now(), 0, EventRecurrenceTypeEnum.ONCE);
    ContactEventJpaEntity entity = ContactEventJpaEntity.fromDomain(event);

    when(jpaContactEventRepository.findById(eventId)).thenReturn(Optional.of(entity));

    // Act
    Optional<ContactEvent> result = jpaContactEventRepositoryAdapter.findById(eventId);

    // Assert
    assertThat(result).isPresent();
    assertThat(result.get().getTitle()).isEqualTo("Title");
    verify(jpaContactEventRepository).findById(eventId);
  }

  @Test
  void findById_ShouldReturnEmptyOptional_WhenEventDoesNotExist() {
    // Arrange
    UUID eventId = UUID.randomUUID();
    when(jpaContactEventRepository.findById(eventId)).thenReturn(Optional.empty());

    // Act
    Optional<ContactEvent> result = jpaContactEventRepositoryAdapter.findById(eventId);

    // Assert
    assertThat(result).isEmpty();
    verify(jpaContactEventRepository).findById(eventId);
  }

  @Test
  void delete_ShouldInvokeRepositoryDelete_WhenIdIsProvided() {
    // Arrange
    UUID eventId = UUID.randomUUID();

    // Act
    jpaContactEventRepositoryAdapter.delete(eventId);

    // Assert
    verify(jpaContactEventRepository).deleteById(eventId);
  }
}
