package com.sparta.deliveryapp.store.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sparta.deliveryapp.store.dto.StoreRequestDto;
import com.sparta.deliveryapp.store.entity.StoreEntity;
import com.sparta.deliveryapp.store.repository.StoreRepository;
import com.sparta.deliveryapp.store.util.kakaoLocal.KakaoLocalAPI;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

class StoreServiceTest {

  @InjectMocks
  private StoreService storeService; // 테스트할 서비스 클래스

  @Mock
  private StoreRepository storeRepository; // Mock 레포지토리

  @Mock
  private KakaoLocalAPI kakaoLocalAPI; // Mock KakaoLocalAPI (주소로 경위도 계산)

  @Mock
  private UserDetailsImpl userDetails; // Mock UserDetailsImpl (로그인한 사용자)

  private StoreRequestDto storeRequestDto; // StoreRequestDto 예시 객체

  private UserDetails userDetailsOwner; // 권한이 OWNER인 사용자 객체
  private UserDetails userDetailsNonOwner; // 권한이 OWNER가 아닌 사용자 객체

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    // 테스트할 DTO 객체 초기화
    storeRequestDto = new StoreRequestDto(
        "Test Store",
        "Test Address",
        "123456789",
        "10:00",
        "22:00"
    );

    // 사용자 정보 준비
    userDetailsOwner = User.builder()
        .username("ownerUser")
        .password("password")
        .authorities(new SimpleGrantedAuthority("ROLE_OWNER"))
        .build();

    userDetailsNonOwner = User.builder()
        .username("nonOwnerUser")
        .password("password")
        .authorities(new SimpleGrantedAuthority("ROLE_USER"))
        .build();
  }

  @Test
  void testRegiStore_WhenUserHasOwnerRole() {
    // Given: OWNER 권한을 가진 사용자
    Mockito.doReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_OWNER")))
        .when(userDetails).getAuthorities();

    // KakaoLocalAPI 호출을 목 처리하여 경위도를 반환
    Mockito.when(kakaoLocalAPI.getCoordsFromAddress(Mockito.anyString()))
        .thenReturn(new double[]{126.9780, 37.5665}); // 예시: 서울시의 경도, 위도

    // 상호명이 이미 등록되어 있지 않은 경우
    Mockito.when(storeRepository.findByStoreName(Mockito.anyString()))
        .thenReturn(Optional.empty());

    // When: 가게 등록 요청
    storeService.regiStore(storeRequestDto, (UserDetailsImpl) userDetails);

    // Then: storeRepository.save()가 호출되었는지 검증
    Mockito.verify(storeRepository).save(Mockito.any(StoreEntity.class));
  }

  @Test
  void testRegiStore_WhenUserDoesNotHaveOwnerRole() {
    // Given: ROLE_USER 권한을 가진 사용자
    Mockito.doReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")))
        .when(userDetails).getAuthorities();

    // When & Then: 권한이 없는 사용자가 가게를 등록하려 할 때 AuthorizationDeniedException이 발생해야 함
    AuthorizationDeniedException exception = assertThrows(AuthorizationDeniedException.class,
        () -> {
          storeService.regiStore(storeRequestDto, (UserDetailsImpl) userDetails);
        });

    assertEquals("가게 등록 권한이 없습니다.", exception.getMessage());
  }

  @Test
  void testRegiStore_WhenStoreNameAlreadyExists() {
    // Given: 이미 존재하는 상호명이 있을 경우
    StoreEntity existingStore = StoreEntity.builder()
        .storeName("Test Store")
        .build();

    // storeRepository의 findByStoreName이 이미 존재하는 상호명을 반환하도록 설정
    Mockito.when(storeRepository.findByStoreName(Mockito.anyString()))
        .thenReturn(Optional.of(existingStore));

    // UserDetails에 ROLE_OWNER 권한 추가
    Mockito.doReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_OWNER")))
        .when(userDetails).getAuthorities();

    // When & Then: 이미 존재하는 상호명으로 가게 등록 시 IllegalArgumentException 발생
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      storeService.regiStore(storeRequestDto, (UserDetailsImpl) userDetails);
    });

    assertEquals("이미 존재하는 상호명입니다.", exception.getMessage());
  }


  @Test
  void testConvertStringToTimestamp() {
    // Given: 시간 문자열 "10:30"
    String timeString = "10:30";

    // When: 문자열을 LocalTime으로 변환
    var result = storeService.convertStringToTimestamp(timeString);

    // Then: 변환된 시간 객체의 값이 10시 30분이어야 함
    assertNotNull(result);
    assertEquals(10, result.getHour());
    assertEquals(30, result.getMinute());
  }
}
