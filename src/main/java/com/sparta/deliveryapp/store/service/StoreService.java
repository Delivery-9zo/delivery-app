package com.sparta.deliveryapp.store.service;

import com.sparta.deliveryapp.store.dto.StoreRequestDto;
import com.sparta.deliveryapp.store.entity.StoreEntity;
import com.sparta.deliveryapp.store.repository.StoreRepository;
import com.sparta.deliveryapp.store.util.kakaoLocal.KakaoLocalAPI;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

  private final StoreRepository storeRepository;
  private final KakaoLocalAPI kakaoLocalAPI;

  /**
   * 가게 정보를 받아 store 테이블에 저장하는 메서드
   *
   * @param : 가게 정보 dto
   */
  @Transactional
  public void regiStore(StoreRequestDto storeRequestDto, UserDetailsImpl userDetails) {

    //유저가 owner권한이 맞는지 체크
    if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_OWNER"))) {
      throw new AuthorizationDeniedException("가게 등록 권한이 없습니다.");
    }

    // 상호명 중복 체크
    Optional<StoreEntity> storeEntity = storeRepository.findByStoreName(
        storeRequestDto.getStoreName());

    if (storeEntity.isPresent()) {
      throw new IllegalArgumentException("이미 존재하는 상호명입니다.");
    }

    // 주소를 기반으로 경위도 추출
    double[] storeCoords = kakaoLocalAPI.getCoordsFromAddress(storeRequestDto.getAddress());

    // StoreEntity 생성 및 엔티티에서 빠진 컬럼 추가
    StoreEntity newStore = StoreEntity.builder().storeName(storeRequestDto.getStoreName())
        .address(storeRequestDto.getAddress()).bRegiNum(storeRequestDto.getBRegiNum())
        .storeCoordX(storeCoords[0]) // 경도
        .storeCoordY(storeCoords[1])  // 위도
        .rating(0.0)
        .openAt(convertStringToTimestamp(storeRequestDto.getOpenAt()))
        .closeAt(convertStringToTimestamp(storeRequestDto.getCloseAt()))
        .user(userDetails.getUser())
        .build();

    // 레포지토리를 이용해서 db에 저장
    storeRepository.save(newStore);

  }

  /**
   * 문자열로 주어진 시간을 LocalTime으로 변환하는 메서드
   *
   * @param: 문자열로 지정된 시간(ex: 10:00)
   * @return: LocalTime
   */
  private LocalTime convertStringToTimestamp(String timeString) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    LocalTime localTime = LocalTime.parse(timeString, formatter);
    return localTime;
  }

}