package com.sparta.deliveryapp.user.entity;


import com.sparta.deliveryapp.auditing.BaseEntity;
import com.sparta.deliveryapp.user.dto.SignUpRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE p_user SET deleted_at=CURRENT_TIMESTAMP WHERE user_id=?")
@SQLRestriction("deleted_at IS NULL")
@Table(name = "p_user")
public class User extends BaseEntity {

  @Id
  @UuidGenerator
  private UUID userId;


  @Column(name = "user_name", nullable = false, length = 50)
  private String userName;

  @Column(name = "nick_name", length = 30)
  private String nickName;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "user_address")
  private String userAddress;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Enumerated(value = EnumType.STRING)
  private UserRole role;


  public User(SignUpRequestDto requestDto, String password) {
    this.userName = requestDto.getUserName();
    this.nickName = requestDto.getNickName();
    this.password = password;
    this.userAddress = requestDto.getAddress();
    this.email = requestDto.getUserEmail();
    this.role = requestDto.getRole();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof User user)) {
      return false;
    }
    return Objects.equals(userId, user.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(userId);
  }
}
