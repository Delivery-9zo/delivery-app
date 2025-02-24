package com.sparta.deliveryapp.ai;

import com.sparta.deliveryapp.user.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AIRepository extends JpaRepository<AI, UUID> {

  List<AI> findByUser(User user);
}
