package com.sparta.deliveryapp.review.entity;

import com.sparta.deliveryapp.auditing.BaseEntity;
import com.sparta.deliveryapp.store.entity.Store;
import com.sparta.deliveryapp.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;


@Entity
@Table(name = "p_review")
@SQLDelete(sql = "UPDATE p_review SET deleted_at=CURRENT_TIMESTAMP WHERE review_uuid=?")
@SQLRestriction("deleted_at IS NULL")
@Getter
public class Review extends BaseEntity {

  @Id
  @UuidGenerator
  @Column(name = "menu_uuid")
  private UUID id;

  @Column(name = "comment", length = 250)
  private String comment;

  @ManyToOne(optional = false)
  @JoinColumn(name = "store_uuid", nullable = false)
  private Store store;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_uuid", nullable = false)
  private User user;

  @Column(name = "review_image")
  private String image;

  @Column(name = "rating")
  private Integer rating;

  protected Review() {
  }

  private Review(String comment, Store store, User user, String image, Integer rating) {
    this.comment = comment;
    this.store = store;
    this.user = user;
    this.image = image;
    this.rating = rating;
  }

  private Review(String comment, Store store, User user, Integer rating) {
    this.comment = comment;
    this.store = store;
    this.user = user;
    this.image = null;
    this.rating = rating;
  }

  public static Review of(String comment, Store store, User user, String image, Integer rating) {
    if (Objects.isNull(image)) {
      return new Review(comment, store, user, rating);
    }

    return new Review(comment, store, user, image, rating);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Review review)) {
      return false;
    }
    return Objects.equals(id, review.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
