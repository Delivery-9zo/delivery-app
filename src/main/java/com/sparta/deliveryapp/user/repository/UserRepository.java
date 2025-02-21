package com.sparta.deliveryapp.user.repository;

import com.sparta.deliveryapp.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String userEmail);


  @Query("SELECT u FROM User u WHERE u.deletedAt IS NOT NULL AND u.deletedAt <= :minusDays")
  List<User> findAllDeletedBefore(LocalDateTime minusDays);

  // 완전히 삭제하기
  @Modifying
  @Transactional
  @Query("DELETE FROM User u WHERE u.deletedAt IS NOT NULL AND u.deletedAt <= :minusDays")
  void deleteAllDeletedBefore(LocalDateTime minusDays);
}
