package com.sparta.deliveryapp.store.entity;

import com.sparta.deliveryapp.auditing.BaseEntity;
import com.sparta.deliveryapp.category.entity.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@SQLDelete(sql = "UPDATE p_storecategory SET deleted_at=CURRENT_TIMESTAMP WHERE storecategory_id=?")
@SQLRestriction("deleted_at IS NULL")
@Table(name = "p_storecategory")
public class StoreCategory extends BaseEntity {

  @Id
  @UuidGenerator
  @Column(name = "storecategory_id")
  private UUID storeCategoryId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "store_id")
  private Store store;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "category_id")
  private Category category;

}
