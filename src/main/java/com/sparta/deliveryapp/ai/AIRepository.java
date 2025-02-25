package com.sparta.deliveryapp.ai;

import com.sparta.deliveryapp.user.entity.User;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AIRepository extends JpaRepository<AI, UUID> {

  void deleteAIByUser(User user);


  @Modifying
  @Transactional
  @Query("UPDATE AI s SET s.deletedAt = CURRENT_TIMESTAMP, s.deletedBy = :deletedBy WHERE s.user.userId = :userId")
  void deleteAIyUser(@Param("deletedBy") String deletedBy, @Param("userId") UUID userId);
}
