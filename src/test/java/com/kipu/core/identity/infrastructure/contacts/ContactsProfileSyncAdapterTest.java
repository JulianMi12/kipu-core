package com.kipu.core.identity.infrastructure.contacts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.application.contact.create.CreateContactCommand;
import com.kipu.core.contacts.application.contact.create.CreateContactUseCase;
import com.kipu.core.contacts.application.contact.get.ContactSummaryResult;
import com.kipu.core.contacts.application.event.birthday.EnsureBirthdayEventUseCase;
import com.kipu.core.contacts.application.tag.create.CreateUserTagCommand;
import com.kipu.core.contacts.application.tag.create.CreateUserTagResult;
import com.kipu.core.contacts.application.tag.create.CreateUserTagUseCase;
import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.repository.ContactRepository;
import com.kipu.core.identity.domain.port.out.ContactProfileInfo;
import java.time.LocalDate;
import java.util.List;
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
class ContactsProfileSyncAdapterTest {

  @Mock private CreateContactUseCase createContactUseCase;
  @Mock private CreateUserTagUseCase createUserTagUseCase;
  @Mock private EnsureBirthdayEventUseCase ensureBirthdayEventUseCase;
  @Mock private ContactRepository contactRepository;
  @Mock private Contact mockContact;

  @InjectMocks private ContactsProfileSyncAdapter contactsProfileSyncAdapter;

  @Test
  @DisplayName("createSelfContact: Should create personal/birthday tags and automate birthday")
  void createSelfContact_FullFlow_WithBirthday() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID personalTagId = UUID.randomUUID();
    UUID birthdayTagId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    String timezone = "America/Bogota";
    LocalDate birthdate = LocalDate.of(1990, 1, 1);

    // Mock para los dos tags que crea el adaptador
    when(createUserTagUseCase.execute(any(CreateUserTagCommand.class)))
        .thenReturn(new CreateUserTagResult(personalTagId, "Personal", "#4F46E5"))
        .thenReturn(new CreateUserTagResult(birthdayTagId, "Cumpleaños", "#EC4899"));

    ContactSummaryResult contactResult =
        new ContactSummaryResult(contactId, "Julian", "Miranda", "j@t.com", Set.of());

    when(createContactUseCase.execute(any(CreateContactCommand.class))).thenReturn(contactResult);

    // Act
    ContactProfileInfo result =
        contactsProfileSyncAdapter.createSelfContact(
            userId, "Julian", "Miranda", "j@t.com", birthdate, timezone);

    // Assert
    assertNotNull(result);
    assertEquals(contactId, result.contactId());

    // Verificar que se crearon 2 tags
    ArgumentCaptor<CreateUserTagCommand> tagCaptor =
        ArgumentCaptor.forClass(CreateUserTagCommand.class);
    verify(createUserTagUseCase, times(2)).execute(tagCaptor.capture());
    List<CreateUserTagCommand> capturedTags = tagCaptor.getAllValues();
    assertEquals("Personal", capturedTags.get(0).name());
    assertEquals("Cumpleaños", capturedTags.get(1).name());

    // Verificar creación de contacto con ambos tags
    ArgumentCaptor<CreateContactCommand> contactCaptor =
        ArgumentCaptor.forClass(CreateContactCommand.class);
    verify(createContactUseCase).execute(contactCaptor.capture());
    assertTrue(contactCaptor.getValue().tagIds().containsAll(Set.of(personalTagId, birthdayTagId)));

    // Verificar automatización de cumpleaños (Rama birth != null)
    verify(ensureBirthdayEventUseCase)
        .execute(any(Contact.class), eq(birthdayTagId), eq(timezone), eq(true));
  }

  @Test
  @DisplayName(
      "createSelfContact: Should handle null birthdate and default timezone (Branch Coverage)")
  void createSelfContact_NoBirthday_DefaultTz() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID pTag = UUID.randomUUID();
    UUID bTag = UUID.randomUUID();

    when(createUserTagUseCase.execute(any(CreateUserTagCommand.class)))
        .thenReturn(new CreateUserTagResult(pTag, "P", "#000"))
        .thenReturn(new CreateUserTagResult(bTag, "B", "#000"));

    when(createContactUseCase.execute(any(CreateContactCommand.class)))
        .thenReturn(new ContactSummaryResult(UUID.randomUUID(), "J", "M", "e", Set.of()));

    // Act - Enviamos birthdate null y timezone blank para cubrir las ternarias del adaptador
    contactsProfileSyncAdapter.createSelfContact(userId, "J", "M", "e", null, "   ");

    // Assert
    // Verificar que NO se llamó a automateBirthday (Rama birth == null)
    verify(ensureBirthdayEventUseCase, times(0)).execute(any(), any(), any(), any(Boolean.class));

    // Verificar que el contacto se creó (aunque no haya cumpleaños)
    verify(createContactUseCase).execute(any(CreateContactCommand.class));
  }

  @Test
  @DisplayName("getContactById: Should return info when contact exists")
  void getContactById_Success() {
    UUID contactId = UUID.randomUUID();
    when(mockContact.getId()).thenReturn(contactId);
    when(mockContact.getFirstName()).thenReturn("Julian");
    when(mockContact.getLastName()).thenReturn("Miranda");
    when(contactRepository.findById(contactId)).thenReturn(Optional.of(mockContact));

    Optional<ContactProfileInfo> result = contactsProfileSyncAdapter.getContactById(contactId);

    assertTrue(result.isPresent());
    assertEquals("Julian", result.get().firstName());
    verify(contactRepository).findById(contactId);
  }

  @Test
  @DisplayName("getContactById: Should return empty Optional when not found")
  void getContactById_NotFound() {
    UUID contactId = UUID.randomUUID();
    when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

    Optional<ContactProfileInfo> result = contactsProfileSyncAdapter.getContactById(contactId);

    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("automateBirthday: Rama timeZone == null (Branch Coverage)")
  void createSelfContact_ShouldUseDefaultUtc_WhenTimezoneIsNull() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID pTag = UUID.randomUUID();
    UUID bTag = UUID.randomUUID();
    LocalDate birthdate = LocalDate.of(1990, 1, 1);

    // Mocks de creación de tags
    when(createUserTagUseCase.execute(any(CreateUserTagCommand.class)))
        .thenReturn(new CreateUserTagResult(pTag, "P", "#000"))
        .thenReturn(new CreateUserTagResult(bTag, "B", "#000"));

    // Mock de creación de contacto
    when(createContactUseCase.execute(any(CreateContactCommand.class)))
        .thenReturn(
            new ContactSummaryResult(UUID.randomUUID(), "Julian", "Miranda", "e", Set.of()));

    // Act - Pasamos timezone estrictamente NULL
    contactsProfileSyncAdapter.createSelfContact(userId, "Julian", "Miranda", "e", birthdate, null);

    // Assert
    // Verificamos que al ser nulo, se haya pasado "UTC" al caso de uso de cumpleaños
    // Esto cubre la rama 'false' del primer chequeo (timeZone != null)
    verify(ensureBirthdayEventUseCase)
        .execute(
            any(Contact.class),
            eq(bTag),
            eq("UTC"), // El valor efectivo esperado
            eq(true));
  }

  @Test
  @DisplayName("automateBirthday: Rama !timeZone.isBlank() == false (Branch Coverage)")
  void createSelfContact_ShouldUseDefaultUtc_WhenTimezoneIsBlank() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID pTag = UUID.randomUUID();
    UUID bTag = UUID.randomUUID();
    LocalDate birthdate = LocalDate.of(1990, 1, 1);

    // El valor es NO NULO pero es BLANCO (solo espacios o vacío)
    String blankTimezone = "   ";

    when(createUserTagUseCase.execute(any(CreateUserTagCommand.class)))
        .thenReturn(new CreateUserTagResult(pTag, "P", "#000"))
        .thenReturn(new CreateUserTagResult(bTag, "B", "#000"));

    when(createContactUseCase.execute(any(CreateContactCommand.class)))
        .thenReturn(
            new ContactSummaryResult(UUID.randomUUID(), "Julian", "Miranda", "e", Set.of()));

    // Act
    contactsProfileSyncAdapter.createSelfContact(
        userId, "Julian", "Miranda", "e", birthdate, blankTimezone);

    // Assert
    // Al ser blanco, el ternario debe evaluar a "UTC"
    verify(ensureBirthdayEventUseCase).execute(any(Contact.class), eq(bTag), eq("UTC"), eq(true));
  }
}
