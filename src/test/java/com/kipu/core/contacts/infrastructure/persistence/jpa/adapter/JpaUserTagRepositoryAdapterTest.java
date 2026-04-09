package com.kipu.core.contacts.infrastructure.persistence.jpa.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.model.UserTag;
import com.kipu.core.contacts.infrastructure.persistence.jpa.entity.UserTagJpaEntity;
import com.kipu.core.contacts.infrastructure.persistence.jpa.repository.JpaUserTagRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaUserTagRepositoryAdapterTest {

  @Mock private JpaUserTagRepository jpaUserTagRepository;

  @InjectMocks private JpaUserTagRepositoryAdapter jpaUserTagRepositoryAdapter;

  @Test
  @DisplayName("save: Should map domain to entity, save and return mapped domain")
  void save_ShouldMapAndReturnSavedTag() {
    // Arrange
    UserTag tag = mock(UserTag.class);
    when(tag.getId()).thenReturn(UUID.randomUUID());
    when(tag.getName()).thenReturn("personal");
    when(tag.getColorHex()).thenReturn("#FFFFFF");

    UserTagJpaEntity entity = UserTagJpaEntity.fromDomain(tag);
    when(jpaUserTagRepository.save(any(UserTagJpaEntity.class))).thenReturn(entity);

    // Act
    UserTag result = jpaUserTagRepositoryAdapter.save(tag);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("personal");
    verify(jpaUserTagRepository).save(any(UserTagJpaEntity.class));
  }

  @Test
  @DisplayName("findById: Should return domain tag when found")
  void findById_ShouldReturnOptionalWithTag_WhenFound() {
    // Arrange
    UUID id = UUID.randomUUID();
    UserTagJpaEntity entity = new UserTagJpaEntity(id, UUID.randomUUID(), "test", "#000");
    when(jpaUserTagRepository.findById(id)).thenReturn(Optional.of(entity));

    // Act
    Optional<UserTag> result = jpaUserTagRepositoryAdapter.findById(id);

    // Assert
    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(id);
  }

  @Test
  @DisplayName("findByOwnerUserId: Should return list of domain tags")
  void findByOwnerUserId_ShouldReturnMappedList() {
    // Arrange
    UUID ownerId = UUID.randomUUID();
    UserTagJpaEntity entity = new UserTagJpaEntity(UUID.randomUUID(), ownerId, "tag", "#000");
    when(jpaUserTagRepository.findByOwnerUserId(ownerId)).thenReturn(List.of(entity));

    // Act
    List<UserTag> results = jpaUserTagRepositoryAdapter.findByOwnerUserId(ownerId);

    // Assert
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getOwnerUserId()).isEqualTo(ownerId);
  }

  @Test
  @DisplayName("delete: Should invoke deleteById in JPA repository")
  void delete_ShouldInvokeDeleteInJpa() {
    // Arrange
    UUID id = UUID.randomUUID();

    // Act
    jpaUserTagRepositoryAdapter.delete(id);

    // Assert
    verify(jpaUserTagRepository).deleteById(id);
  }

  @Test
  @DisplayName("existsByNameAndOwnerUserId: Should return result from JPA repository")
  void existsByNameAndOwnerUserId_ShouldReturnBoolean() {
    // Arrange
    String name = "personal";
    UUID ownerId = UUID.randomUUID();
    when(jpaUserTagRepository.existsByNameAndOwnerUserId(name, ownerId)).thenReturn(true);

    // Act
    boolean exists = jpaUserTagRepositoryAdapter.existsByNameAndOwnerUserId(name, ownerId);

    // Assert
    assertThat(exists).isTrue();
    verify(jpaUserTagRepository).existsByNameAndOwnerUserId(name, ownerId);
  }

  @Test
  @DisplayName("findById: Should return empty Optional when tag not found")
  void findById_ShouldReturnEmpty_WhenNotFound() {
    // Arrange
    UUID id = UUID.randomUUID();
    when(jpaUserTagRepository.findById(id)).thenReturn(Optional.empty());

    // Act
    Optional<UserTag> result = jpaUserTagRepositoryAdapter.findById(id);

    // Assert
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName(
      "findByOwnerUserIdAndNameIgnoreCase: Should return domain tag when found by owner and name")
  void findByOwnerUserIdAndNameIgnoreCase_ShouldReturnTag_WhenFound() {
    // Arrange
    UUID ownerId = UUID.randomUUID();
    UUID tagId = UUID.randomUUID();
    String name = "CUMPLEAÑOS";
    UserTagJpaEntity entity = new UserTagJpaEntity(tagId, ownerId, "Cumpleaños", "#EC4899");

    when(jpaUserTagRepository.findByOwnerUserIdAndNameIgnoreCase(ownerId, name))
        .thenReturn(Optional.of(entity));

    // Act
    Optional<UserTag> result =
        jpaUserTagRepositoryAdapter.findByOwnerUserIdAndNameIgnoreCase(ownerId, name);

    // Assert
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("cumpleaños");
    assertThat(result.get().getId()).isEqualTo(tagId);
    verify(jpaUserTagRepository).findByOwnerUserIdAndNameIgnoreCase(ownerId, name);
  }

  @Test
  @DisplayName("findByOwnerUserIdAndNameIgnoreCase: Should return empty when not found")
  void findByOwnerUserIdAndNameIgnoreCase_ShouldReturnEmpty_WhenNotFound() {
    // Arrange
    UUID ownerId = UUID.randomUUID();
    String name = "Inexistente";

    when(jpaUserTagRepository.findByOwnerUserIdAndNameIgnoreCase(ownerId, name))
        .thenReturn(Optional.empty());

    // Act
    Optional<UserTag> result =
        jpaUserTagRepositoryAdapter.findByOwnerUserIdAndNameIgnoreCase(ownerId, name);

    // Assert
    assertThat(result).isEmpty();
    verify(jpaUserTagRepository).findByOwnerUserIdAndNameIgnoreCase(ownerId, name);
  }

  @Test
  @DisplayName("findAllById: Should return list of domain tags when IDs exist")
  void findAllById_ShouldReturnMappedList_WhenIdsExist() {
    // Arrange
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    List<UUID> ids = List.of(id1, id2);

    UserTagJpaEntity entity1 = new UserTagJpaEntity(id1, UUID.randomUUID(), "Personal", "#4F46E5");
    UserTagJpaEntity entity2 =
        new UserTagJpaEntity(id2, UUID.randomUUID(), "Cumpleaños", "#EC4899");

    when(jpaUserTagRepository.findAllById(ids)).thenReturn(List.of(entity1, entity2));

    // Act
    List<UserTag> results = jpaUserTagRepositoryAdapter.findAllById(ids);

    // Assert
    assertThat(results).hasSize(2);
    assertThat(results.get(0).getId()).isEqualTo(id1);
    assertThat(results.get(1).getId()).isEqualTo(id2);
    verify(jpaUserTagRepository).findAllById(ids);
  }

  @Test
  @DisplayName("findAllById: Should return empty list when no IDs match")
  void findAllById_ShouldReturnEmptyList_WhenNoMatch() {
    // Arrange
    List<UUID> ids = List.of(UUID.randomUUID());
    when(jpaUserTagRepository.findAllById(ids)).thenReturn(List.of());

    // Act
    List<UserTag> results = jpaUserTagRepositoryAdapter.findAllById(ids);

    // Assert
    assertThat(results).isEmpty();
    verify(jpaUserTagRepository).findAllById(ids);
  }
}
