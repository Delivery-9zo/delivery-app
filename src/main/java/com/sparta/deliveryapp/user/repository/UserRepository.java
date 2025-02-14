package com.sparta.deliveryapp.user.repository;

import com.sparta.deliveryapp.user.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

}
