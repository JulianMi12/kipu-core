package com.kipu.core.contacts.infrastructure.persistence.jpa.entity;

import com.kipu.core.contacts.domain.model.UserTag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_tags", schema = "contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTagJpaEntity {

  @Id
  @Column(nullable = false, updatable = false)
  private UUID id;

  @Column(name = "owner_user_id", nullable = false, updatable = false)
  private UUID ownerUserId;

  @Column(name = "name", nullable = false, length = 50)
  private String name;

  @Column(name = "color_hex", length = 7)
  private String colorHex;

  public static UserTagJpaEntity fromDomain(UserTag tag) {
    return new UserTagJpaEntity(
        tag.getId(), tag.getOwnerUserId(), tag.getName(), tag.getColorHex());
  }

  public UserTag toDomain() {
    return UserTag.reconstitute(id, ownerUserId, name, colorHex);
  }
}
