package com.sparta.deliveryapp.menu.entity;

import com.sparta.deliveryapp.auditing.BaseEntity;
import com.sparta.deliveryapp.store.entity.Store;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "p_menu")
@Getter
@Setter
public class Menu extends BaseEntity {

  @Id
  @UuidGenerator
  @Column(name = "menu_uuid")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "store_uuid", nullable = false)
  private Store store;

  @Column(name = "menu_name", length = 100)
  private String name;

  @Column(name = "menu_info", length = 200)
  private String info;

  @Column(name = "price")
  private Long price;

  @Column(name = "image")
  private String image;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Menu menu)) {
      return false;
    }
    return Objects.equals(id, menu.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
