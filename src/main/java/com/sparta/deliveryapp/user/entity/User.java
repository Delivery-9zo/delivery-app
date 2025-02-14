package com.sparta.deliveryapp.user.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_user")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
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


}
