package com.sparta.deliveryapp.store.service;

import com.sparta.deliveryapp.store.dto.StoreRequestDto;
import com.sparta.deliveryapp.store.dto.StoreResponseDto;
import com.sparta.deliveryapp.store.entity.Store;
import com.sparta.deliveryapp.store.repository.StoreRepository;
import com.sparta.deliveryapp.store.util.kakaoLocal.KakaoLocalAPI;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
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
//    if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_OWNER"))) {
//      throw new AuthorizationDeniedException("가게 등록 권한이 없습니다.");
//    }

    // 상호명 중복 체크
    Optional<Store> storeEntity = storeRepository.findByStoreName(
        storeRequestDto.getStoreName());

    if (storeEntity.isPresent()) {
      throw new IllegalArgumentException("이미 존재하는 상호명입니다.");
    }

    // 주소를 기반으로 경위도 추출
    double[] storeCoords = kakaoLocalAPI.getCoordsFromAddress(storeRequestDto.getAddress());

    // StoreEntity 생성 및 엔티티에서 빠진 컬럼 추가
    Store newStore = Store.builder()
        .storeName(storeRequestDto.getStoreName())
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
   * 가게 uuid를 기반으로 가게 정보 소프트 삭제하는 메서드
   *
   * @param: 가게 uuid(storeId), 유저 정보(userDetails)
   */
  @Transactional
  public Store deleteStore(String storeId, UserDetailsImpl userDetails) {
//    if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MASTER"))) {
//      throw new AuthorizationDeniedException("가게 삭제 권한이 없습니다.");
//    }

    Store storeEntity = storeRepository.findByStoreId(UUID.fromString(storeId))
        .orElseThrow(() -> new EntityNotFoundException(storeId + " 가게가 존재하지 않습니다."));

    storeEntity.onPreRemove();

    return storeRepository.save(storeEntity);

  }

  /**
   * 가게 이름으로 등록된 모든 가게 검색
   *
   * @param: 가게 이름 (storeName)
   * @return: StoreResponseDto 리스트
   */
  public List<StoreResponseDto> findStoresByStoreName(String storeName,
      UserDetailsImpl userDetails) {

//    if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MASTER"))) {
//      throw new AuthorizationDeniedException("가게 목록 조회 권한이 없습니다.");
//    }
    log.info("findStoresByStoreName 호출");
    List<Store> stores = storeRepository.findByStoreNameContaining(storeName);

    if (stores.isEmpty()) {
      throw new NoSuchElementException("해당하는 가게가 없습니다.");
    }

    List<StoreResponseDto> storeResponseDtos = stores.stream()
        .map(store -> StoreResponseDto.builder()
            .storeId(store.getStoreId())
            .storeName(store.getStoreName())
            .address(store.getAddress())
            .bRegiNum(store.getBRegiNum())
            .openAt(store.getOpenAt())
            .closeAt(store.getCloseAt())
            .distanceFromRequest(10.0)  // 요청한 위치에서 최대 10km까지로 fix
            .build())
        .toList();

    return storeResponseDtos;
  }

  public StoreResponseDto findStoresByStoreId(String storeId, UserDetailsImpl userDetails) {
    Optional<Store> store = storeRepository.findByStoreId(UUID.fromString(storeId));

    StoreResponseDto storeResponseDto = store.map(s-> StoreResponseDto.builder()
        .storeId(s.getStoreId())
        .storeName(s.getStoreName())
        .address(s.getAddress())
        .bRegiNum(s.getBRegiNum())
        .openAt(s.getOpenAt())
        .closeAt(s.getCloseAt())
        .build())
        .orElseThrow(() -> new NoSuchElementException("해당하는 가게가 없습니다."));

    return storeResponseDto;
  }


  /**
   * 문자열로 주어진 시간을 LocalTime으로 변환하는 메서드
   *
   * @param: 문자열로 지정된 시간(ex: 10:00)
   * @return: LocalTime
   */
  LocalTime convertStringToTimestamp(String timeString) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    LocalTime localTime = LocalTime.parse(timeString, formatter);
    return localTime;
  }


}