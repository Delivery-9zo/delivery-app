package com.sparta.deliveryapp.region.repository;

import com.sparta.deliveryapp.region.entity.RegionEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<RegionEntity, Integer> {

}
