package com.sparta.deliveryapp.store.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.deliveryapp.store.dto.StoreRequestDto;
import com.sparta.deliveryapp.store.entity.StoreEntity;
import com.sparta.deliveryapp.store.repository.StoreRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService {

  private final StoreRepository storeRepository;
  private final ObjectMapper objectMapper; // Jackson ObjectMapper

  /**
   * 가게 정보를 받아 store 테이블에 저장하는 메서드
   *
   * @Param : 가게 정보 dto
   */
  @Transactional
  public void regiStore(StoreRequestDto storeRequestDto) {
    // 상호명 중복 체크
    Optional<StoreEntity> storeEntity = storeRepository.findByStoreName(
        storeRequestDto.getStoreName());

    if (storeEntity.isPresent()) {
      throw new IllegalArgumentException("이미 존재하는 상호명입니다.");
    }

    // 주소를 기반으로 위경도 추출


    // StoreEntity 생성 및 엔티티에서 빠진 컬럼 추가

    //레포지토리를 이용해서 db에 저장

  }

}