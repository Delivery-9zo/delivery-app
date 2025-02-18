package com.sparta.deliveryapp.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sparta.deliveryapp.store.dto.StoreRequestDto;
import com.sparta.deliveryapp.store.entity.StoreEntity;
import com.sparta.deliveryapp.store.repository.StoreRepository;
import com.sparta.deliveryapp.store.service.StoreService;
import com.sparta.deliveryapp.store.util.kakaoLocal.KakaoLocalAPI;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class StoreServiceTest {

  @Mock
  private StoreRepository storeRepository;

  @Mock
  private KakaoLocalAPI kakaoLocalAPI;

  @InjectMocks
  private StoreService storeService; // regStore 메서드를 포함한 클래스

  @Mock
  private UserDetailsImpl userDetails;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this); // Mock 초기화
  }

  @Test
  void testRegiStore() {
    // Given
    StoreRequestDto storeRequestDto = new StoreRequestDto("Test Store", "123 Business Regi Number",
        "서울특별시 강남구 역삼동", "10:00", "22:00");
    when(kakaoLocalAPI.getCoordsFromAddress(storeRequestDto.getAddress())).thenReturn(
        new double[]{127.0276, 37.4979}); // 가상의 경위도

    // When
    storeService.regiStore(storeRequestDto, userDetails);

    // Then
    verify(storeRepository, times(1)).save(any(StoreEntity.class)); // DB에 저장이 한 번 호출됐는지 확인

    // StoreEntity가 제대로 생성되었는지 검증
    verify(kakaoLocalAPI).getCoordsFromAddress(storeRequestDto.getAddress());
    assertNotNull(storeRequestDto.getStoreName());
  }

  @Test
  void testGetCoordsFromAddress_invalidAddress() {
    // Given
    when(kakaoLocalAPI.getCoordsFromAddress("Invalid Address")).thenThrow(
        new IllegalArgumentException("유효하지 않은 주소입니다."));

    // When & Then
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      kakaoLocalAPI.getCoordsFromAddress("Invalid Address");
    });

    assertEquals("유효하지 않은 주소입니다.", exception.getMessage());
  }

  @Test
  void testStoreNameDuplicate() {
    // Given
    StoreRequestDto storeRequestDto = new StoreRequestDto("Existing Store",
        "123 Business Regi Number", "서울특별시 강남구 역삼동", "09:00", "21:00");
    StoreEntity existingStore = new StoreEntity(); // 이미 존재하는 상호명
    when(storeRepository.findByStoreName(storeRequestDto.getStoreName())).thenReturn(
        Optional.of(existingStore));

    // When & Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      storeService.regiStore(storeRequestDto, userDetails);
    });

    assertEquals("이미 존재하는 상호명입니다.", exception.getMessage());
  }
}

