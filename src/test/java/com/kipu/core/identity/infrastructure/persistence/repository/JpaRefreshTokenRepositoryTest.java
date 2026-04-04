package com.kipu.core.identity.infrastructure.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.kipu.core.identity.infrastructure.persistence.jpa.entity.RefreshTokenJpaEntity;
import com.kipu.core.identity.infrastructure.persistence.jpa.repository.JpaRefreshTokenRepository;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(
    properties = {"spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=create-drop"})
class JpaRefreshTokenRepositoryTest {

  @Autowired private JpaRefreshTokenRepository repository;
  @Autowired private TestEntityManager entityManager;

  @Test
  void deleteByUserId_ShouldRemoveTokens() {
    // Arrange
    UUID userId = UUID.randomUUID();
    RefreshTokenJpaEntity token = new RefreshTokenJpaEntity();
    token.setId(UUID.randomUUID());
    token.setUserId(userId);
    token.setToken("some-token");
    token.setExpiryDate(OffsetDateTime.now().plusDays(1));

    entityManager.persist(token);
    entityManager.flush();

    // Act
    repository.deleteByUserId(userId);

    // Assert
    assertThat(repository.findAll()).isEmpty();
  }

  @Test
  void revokeByToken_ShouldSetRevokedToTrue() {
    // Arrange
    String tokenStr = "active-token";
    RefreshTokenJpaEntity token = new RefreshTokenJpaEntity();
    token.setId(UUID.randomUUID());
    token.setToken(tokenStr);
    token.setRevoked(false);
    token.setExpiryDate(OffsetDateTime.now().plusDays(1));
    token.setUserId(UUID.randomUUID());

    entityManager.persist(token);
    entityManager.flush();

    // Act
    repository.revokeByToken(tokenStr);
    entityManager.clear();

    // Assert
    RefreshTokenJpaEntity updated = repository.findAll().get(0);
    assertThat(updated.isRevoked()).isTrue();
  }

  @Test
  void existsByTokenAndRevokedFalseAndExpiryDateAfter_ShouldReturnTrue_WhenTokenIsValid() {
    // Arrange
    String tokenStr = "valid-token";
    OffsetDateTime now = OffsetDateTime.now();

    RefreshTokenJpaEntity token = new RefreshTokenJpaEntity();
    token.setId(UUID.randomUUID());
    token.setToken(tokenStr);
    token.setRevoked(false);
    token.setExpiryDate(now.plusHours(1));
    token.setUserId(UUID.randomUUID());

    entityManager.persist(token);
    entityManager.flush();

    // Act
    boolean exists = repository.existsByTokenAndRevokedFalseAndExpiryDateAfter(tokenStr, now);

    // Assert
    assertThat(exists).isTrue();
  }

  @Test
  void existsByTokenAndRevokedFalseAndExpiryDateAfter_ShouldReturnFalse_WhenTokenIsExpired() {
    // Arrange
    String tokenStr = "expired-token";
    OffsetDateTime now = OffsetDateTime.now();

    RefreshTokenJpaEntity token = new RefreshTokenJpaEntity();
    token.setId(UUID.randomUUID());
    token.setToken(tokenStr);
    token.setRevoked(false);
    token.setExpiryDate(now.minusHours(1));
    token.setUserId(UUID.randomUUID());

    entityManager.persist(token);
    entityManager.flush();

    // Act
    boolean exists = repository.existsByTokenAndRevokedFalseAndExpiryDateAfter(tokenStr, now);

    // Assert
    assertThat(exists).isFalse();
  }
}
