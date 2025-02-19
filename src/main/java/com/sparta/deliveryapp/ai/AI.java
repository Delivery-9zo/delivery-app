package com.sparta.deliveryapp.ai;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@Table(name = "p_ai")
public class AI {

  @Id
  @UuidGenerator
  private UUID aiId;

  @Column(columnDefinition = "TEXT")  // TEXT 타입으로 지정
  private String answer;

  @Column(columnDefinition = "TEXT")  // TEXT 타입으로 지정
  private String prompt;

  private LocalDateTime createdAt;

}
