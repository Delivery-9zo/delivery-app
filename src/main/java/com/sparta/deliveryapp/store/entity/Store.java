package com.sparta.deliveryapp.store.entity;

import com.sparta.deliveryapp.auditing.BaseEntity;
import com.sparta.deliveryapp.category.entity.Category;
import com.sparta.deliveryapp.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.Where;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Where(clause = "deleted_at IS NULL")
@Table(name = "p_store")
public class Store extends BaseEntity {

  @Id
  @UuidGenerator
  private UUID storeId;

  @Getter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "store_name")
  private String storeName;

  @Column(name = "address")
  private String address;

  @Column(name = "b_regi_num", nullable = false)
  private String bRegiNum;

  @Column(name = "open_at")
  private LocalTime openAt;

  @Column(name = "close_at")
  private LocalTime closeAt;

  @Column(name = "store_x")
  private Double storeCoordX; // 경도

  @Column(name = "store_y")
  private Double storeCoordY; // 위도

  public boolean isNotAssociated(User user) {
    return !isAssociated(user);
  }

  public boolean isAssociated(User user) {
    return this.user.equals(user);
  }

  @OneToMany(mappedBy = "store")
  private List<StoreCategory> storeCategories;

  @Transient
  public List<Category> getCategories() {
    List<Category> categories = new ArrayList<>();
    for (StoreCategory storeCategory : storeCategories) {
      categories.add(storeCategory.getCategory());
    }
    return categories;
  }

}
