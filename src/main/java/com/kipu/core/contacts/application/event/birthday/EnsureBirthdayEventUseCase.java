package com.kipu.core.contacts.application.event.birthday;

import com.kipu.core.contacts.application.event.create.CreateContactEventCommand;
import com.kipu.core.contacts.application.event.create.CreateContactEventUseCase;
import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import com.kipu.core.contacts.domain.repository.ContactEventRepository;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnsureBirthdayEventUseCase {

  private final CreateContactEventUseCase createContactEventUseCase;
  private final ContactEventRepository contactEventRepository;

  public void execute(Contact contact, UUID birthdayTagId, String timezone, boolean isSelf) {
    if (contact.getBirthdate() == null) return;

    ZoneId userZone = ZoneId.of(timezone != null ? timezone : "UTC");
    ZonedDateTime nextBirthday = calculateNextBirthday(contact, userZone);

    contactEventRepository
        .findByContactIdAndTagIdsContains(contact.getId(), birthdayTagId)
        .ifPresentOrElse(
            existingEvent -> updateBirthdayEvent(existingEvent, nextBirthday),
            () -> createBirthdayEvent(contact, birthdayTagId, nextBirthday, userZone, isSelf));
  }

  private ZonedDateTime calculateNextBirthday(Contact contact, ZoneId userZone) {
    ZonedDateTime now = ZonedDateTime.now(userZone);
    ZonedDateTime birthdayThisYear =
        contact.getBirthdate().atTime(LocalTime.of(9, 0)).atZone(userZone).withYear(now.getYear());

    return birthdayThisYear.isBefore(now) ? birthdayThisYear.plusYears(1) : birthdayThisYear;
  }

  private void updateBirthdayEvent(ContactEvent existingEvent, ZonedDateTime nextBirthday) {
    log.info(
        "[EnsureBirthdayEventUseCase] Updating existing birthday event for contact: {}",
        existingEvent.getContactId());

    existingEvent.updateStartDateTime(nextBirthday.toOffsetDateTime());
    contactEventRepository.save(existingEvent);
  }

  private void createBirthdayEvent(
      Contact contact,
      UUID birthdayTagId,
      ZonedDateTime nextBirthday,
      ZoneId userZone,
      boolean isSelf) {
    log.info(
        "[EnsureBirthdayEventUseCase] Creating new birthday event for contact: {}",
        contact.getId());

    String eventTitle =
        isSelf ? "¡Feliz Cumpleaños! 🎂" : "Cumpleaños de " + contact.getFirstName() + " 🎂";
    String eventDescription =
        isSelf
            ? "¡Hoy celebramos tu vida! Disfruta de tu día al máximo."
            : "Es el día especial de "
                + contact.getFirstName()
                + ". ¡No olvides enviarle un mensaje!";

    createContactEventUseCase.execute(
        new CreateContactEventCommand(
            contact.getOwnerUserId(),
            contact.getId(),
            eventTitle,
            eventDescription,
            nextBirthday.toOffsetDateTime(),
            0,
            EventRecurrenceTypeEnum.YEARLY,
            1,
            userZone.getId(),
            Set.of(birthdayTagId)));
  }
}
