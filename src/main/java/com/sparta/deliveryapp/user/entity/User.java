package com.sparta.deliveryapp.user.entity;


import com.sparta.deliveryapp.user.dto.SignUpRequestDto;
import com.sparta.deliveryapp.user.dto.UserUpdateRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_user")
public class User {

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

  public void update(UserUpdateRequestDto requestDto, String password) {
    this.userName = requestDto.getUserName();
    this.nickName = requestDto.getNickName();
    this.password = password;
    this.userAddress = requestDto.getAddress();
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
