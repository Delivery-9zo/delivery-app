package com.sparta.deliveryapp.category.entity;

import com.sparta.deliveryapp.auditing.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@SQLDelete(sql = "UPDATE p_category SET deleted_at=CURRENT_TIMESTAMP WHERE category_id=?")
@SQLRestriction("deleted_at IS NULL")
@Table(name = "p_category")
public class Category extends BaseEntity {

  @Id
  @UuidGenerator
  @Column(name = "category_id")
  private UUID categoryId;

  @NotBlank
  @Column(unique = true)
  private String categoryName;

}
