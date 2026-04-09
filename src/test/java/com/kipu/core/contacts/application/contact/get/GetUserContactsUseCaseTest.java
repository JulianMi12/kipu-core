package com.kipu.core.contacts.application.contact.get;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.repository.ContactRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class GetUserContactsUseCaseTest {

  @Mock private ContactRepository contactRepository;

  @InjectMocks private GetUserContactsUseCase getUserContactsUseCase;

  @Test
  @DisplayName("execute: Should return a paged list of contacts mapped to ContactSummaryResult")
  void execute_ShouldReturnPagedContacts_WhenCalled() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID selfContactId = UUID.randomUUID();
    Pageable pageable = PageRequest.of(0, 10);

    Contact contact =
        Contact.reconstitute(
            UUID.randomUUID(),
            userId,
            "Julian",
            "Miranda",
            "dev@test.com",
            null,
            Map.of(),
            Set.of(),
            OffsetDateTime.now());

    Page<Contact> contactPage = new PageImpl<>(List.of(contact));

    when(contactRepository.findAllByOwnerUserId(userId, selfContactId, pageable))
        .thenReturn(contactPage);

    // Act
    Page<ContactSummaryResult> result =
        getUserContactsUseCase.execute(userId, selfContactId, pageable);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).firstName()).isEqualTo("Julian");

    verify(contactRepository).findAllByOwnerUserId(userId, selfContactId, pageable);
  }

  @Test
  @DisplayName("execute: Should handle empty results from repository")
  void execute_ShouldReturnEmptyPage_WhenNoContactsFound() {
    // Arrange
    UUID userId = UUID.randomUUID();
    Pageable pageable = PageRequest.of(0, 10);

    when(contactRepository.findAllByOwnerUserId(userId, null, pageable)).thenReturn(Page.empty());

    // Act
    Page<ContactSummaryResult> result = getUserContactsUseCase.execute(userId, null, pageable);

    // Assert
    assertThat(result).isEmpty();
    verify(contactRepository).findAllByOwnerUserId(userId, null, pageable);
  }
}
