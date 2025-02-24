package com.sparta.deliveryapp.store.service;

import com.sparta.deliveryapp.category.entity.Category;
import com.sparta.deliveryapp.category.repository.CategoryRepository;
import com.sparta.deliveryapp.store.dto.StoreNearbyStoreResponseDto;
import com.sparta.deliveryapp.store.dto.StoreNearbyStoreWithCategoryResponseDto;
import com.sparta.deliveryapp.store.dto.StoreRequestDto;
import com.sparta.deliveryapp.store.dto.StoreResponseDto;
import com.sparta.deliveryapp.store.entity.Store;
import com.sparta.deliveryapp.store.entity.StoreCategory;
import com.sparta.deliveryapp.store.repository.StoreCategoryRepository;
import com.sparta.deliveryapp.store.repository.StoreRepository;
import com.sparta.deliveryapp.store.util.kakaoLocal.KakaoLocalAPI;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

  private final StoreRepository storeRepository;
  private final CategoryRepository categoryRepository;
  private final StoreCategoryRepository storeCategoryRepository;
  private final KakaoLocalAPI kakaoLocalAPI;

  /**
   * 가게 정보를 받아 store 테이블에 저장하는 메서드
   *
   * @param : 가게 정보 dto
   */
  @Transactional
  public void regiStore(StoreRequestDto storeRequestDto, UserDetailsImpl userDetails) {

    // 상호명 중복 체크
    Optional<Store> storeEntity = storeRepository.findByStoreName(
        storeRequestDto.getStoreName());

    if (storeEntity.isPresent()) {
      throw new IllegalArgumentException("이미 존재하는 상호명입니다.");
    }

    // 주소를 기반으로 경위도 추출
    double[] storeCoords = kakaoLocalAPI.getCoordsFromAddress(storeRequestDto.getAddress());

    // 카테고리 이름 리스트로 카테고리 테이블 조회
    List<Category> categories = categoryRepository.findByCategoryNameIn(
        storeRequestDto.getCategories());

    // StoreEntity 생성 및 엔티티에서 빠진 컬럼 추가
    Store newStore = Store.builder()
        .storeName(storeRequestDto.getStoreName())
        .address(storeRequestDto.getAddress()).bRegiNum(storeRequestDto.getBRegiNum())
        .storeCoordX(storeCoords[0]) // x:경도
        .storeCoordY(storeCoords[1])  // y:위도
        .openAt(convertStringToTimestamp(storeRequestDto.getOpenAt()))
        .closeAt(convertStringToTimestamp(storeRequestDto.getCloseAt()))
        .user(userDetails.getUser())
        .build();

    // 카테고리 리스트를 받아서 중간 테이블과의 관계 설정
    List<StoreCategory> storeCategories = createStoreCategories(categories,
        newStore);  // newStore 객체 전달

    newStore.setStoreCategories(storeCategories);

    // 레포지토리를 이용해서 db에 저장
    storeRepository.save(newStore);

  }

  // 중간 테이블 연결
  private List<StoreCategory> createStoreCategories(List<Category> categories, Store store) {
    List<StoreCategory> storeCategories = new ArrayList<>();
    for (Category category : categories) {
      StoreCategory storeCategory = new StoreCategory();
      storeCategory.setStore(store);  // Store-StoreCategory 연결
      storeCategory.setCategory(category);  // StoreCategory-Category 연결
      storeCategories.add(storeCategory);
    }
    return storeCategories;
  }

  /**
   * 가게 uuid를 기반으로 가게 정보 소프트 삭제하는 메서드
   *
   * @param: 가게 uuid(storeId), 유저 정보(userDetails)
   */
  @Transactional
  public void deleteStore(String storeId) {

    Store storeEntity = storeRepository.findByStoreId(UUID.fromString(storeId))
        .orElseThrow(() -> new EntityNotFoundException(storeId + " 가게가 존재하지 않습니다."));

    if(storeEntity.getDeletedAt() != null){
      throw new IllegalArgumentException("이미 삭제된 가게입니다.");
    }

    storeRepository.delete(storeEntity);
  }

  /**
   * 가게 이름으로 등록된 모든 가게 검색
   *
   * @param: 가게 이름 (storeName)
   * @return: StoreResponseDto 리스트
   */
  public Page<StoreResponseDto> findStoresByStoreName(String storeName, Pageable pageable) {

    Page<Store> stores = storeRepository.findByStoreNameContainingWithCategories(storeName,
        pageable);

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
            .categories(store.getStoreCategories().stream()
                .map(storeCategory -> storeCategory.getCategory().getCategoryName())
                .collect(Collectors.toList()))
            .build())
        .toList();

    return new PageImpl<>(storeResponseDtos, pageable, stores.getTotalElements());
  }

  public StoreResponseDto findStoresByStoreId(UUID storeId) {
    Store store = storeRepository.findByStoreId(storeId)
        .orElseThrow(() -> new NoSuchElementException("해당하는 가게가 없습니다."));

    StoreResponseDto storeResponseDto = StoreResponseDto.builder()
            .storeId(store.getStoreId())
            .storeName(store.getStoreName())
            .address(store.getAddress())
            .bRegiNum(store.getBRegiNum())
            .openAt(store.getOpenAt())
            .closeAt(store.getCloseAt())
            .categories(store.getStoreCategories().stream()
                .map(storeCategory -> storeCategory.getCategory().getCategoryName())
                .collect(Collectors.toList()))
            .build();

    return storeResponseDto;
  }

  public Page<StoreNearbyStoreResponseDto> findNearbyStoresWithoutCategory(double longitude,
      double latitude,
      Pageable pageable) {
    final int RANGE = 3000;

    if (getDecimalPlaces(longitude) < 2 || getDecimalPlaces(latitude) < 2) {
      throw new IllegalArgumentException("주어진 좌표의 자릿수가 너무 작습니다.");
    }

    Page<StoreNearbyStoreResponseDto> nearbyStores = storeRepository.findNearbyStoresWithoutCategory(
        longitude,
        latitude, RANGE, pageable);

    if (nearbyStores.isEmpty()) {
      throw new NoSuchElementException("근처 가게가 없습니다.");
    }

    return nearbyStores;

  }

  /**
   * 실수형 자릿수를 체크하는 메서드
   *
   * @param: 실수형
   * @return: 소숫점 아래 자릿수 반환
   */
  public static int getDecimalPlaces(double value) {
    String valueStr = Double.toString(value);
    int decimalIndex = valueStr.indexOf('.');

    if (decimalIndex == -1) {
      return 0;
    }

    return valueStr.length() - decimalIndex - 1;
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


  public Page<StoreNearbyStoreWithCategoryResponseDto> findNearbyStoresByCategory(
      List<String> categoryNames,
      double longitude, double latitude, Pageable pageable) {
    final int RANGE = 3000;

    if (getDecimalPlaces(longitude) < 2 || getDecimalPlaces(latitude) < 2) {
      throw new IllegalArgumentException("주어진 좌표의 자릿수가 너무 작습니다.");
    }

    Page<StoreNearbyStoreWithCategoryResponseDto> nearbyStores = storeRepository.findNearbyStoresByCategories(
        categoryNames,
        longitude,
        latitude,
        RANGE,
        pageable);

    if (nearbyStores.isEmpty()) {
      throw new NoSuchElementException("카테고리에 해당하는 근처 가게가 없습니다.");
    }

    return nearbyStores;
  }

  //master 권한 가게 정보 업데이트
  public void updateStoreMaster(StoreRequestDto storeRequestDto, UserDetailsImpl userDetails) {

    // 가게 유무 체크
    Store existingStore = storeRepository.findByStoreName(storeRequestDto.getStoreName())
        .orElseThrow(() -> new NoSuchElementException("가게 정보를 찾을 수 없습니다."));

    // 카테고리 이름 리스트로 카테고리 테이블 조회(카테고리 검증)
    List<Category> categories = categoryRepository.findByCategoryNameIn(
        storeRequestDto.getCategories());

    // 기존 StoreCategory 가져오기
    List<StoreCategory> existingCategories = existingStore.getStoreCategories();

    // 기존 카테고리 이름 목록
    Set<String> existingCategoryNames = existingCategories.stream()
        .map(sc -> sc.getCategory().getCategoryName())
        .collect(Collectors.toSet());

    // 새로 요청된 카테고리 이름 목록
    Set<String> newCategoryNames = categories.stream()
        .map(Category::getCategoryName)
        .collect(Collectors.toSet());

    // 삭제할 카테고리 찾기 (기존에는 있지만 새로운 요청에는 없는)
    List<StoreCategory> categoriesToRemove = existingCategories.stream()
        .filter(sc -> !newCategoryNames.contains(sc.getCategory().getCategoryName()))
        .collect(Collectors.toList());

    // 추가할 카테고리 찾기 (새로운 요청에는 있지만 기존에는 없는)
    List<Category> categoriesToAdd = categories.stream()
        .filter(c -> !existingCategoryNames.contains(c.getCategoryName()))
        .collect(Collectors.toList());

    // 삭제 로직
    categoriesToRemove.forEach(existingStore::removeStoreCategory);
    storeCategoryRepository.deleteAll(categoriesToRemove);

    // 추가 로직
    List<StoreCategory> newStoreCategories = createStoreCategories(categoriesToAdd, existingStore);
    newStoreCategories.forEach(existingStore::addStoreCategory);

    // 변경할 필드만 수정
    existingStore = Store.builder()
        .storeId(existingStore.getStoreId())
        .storeName(storeRequestDto.getStoreName())
        .address(storeRequestDto.getAddress())
        .bRegiNum(storeRequestDto.getBRegiNum())
        .openAt(convertStringToTimestamp(storeRequestDto.getOpenAt()))
        .closeAt(convertStringToTimestamp(storeRequestDto.getCloseAt()))
        .user(userDetails.getUser())
        .storeCategories(existingStore.getStoreCategories())
        .build();

    // 업데이트된 엔티티 저장
    storeRepository.save(existingStore);
  }


  //owner 권한 가게 정보 수정
  public void updateStore(StoreRequestDto storeRequestDto, UserDetailsImpl userDetails) {
    // 유저 가게 정보 조회
    List<Store> storeEntity = storeRepository.findByUser(userDetails.getUser());
    Store userStore = storeEntity.stream()
        .filter(store -> store.getStoreName().equals(storeRequestDto.getStoreName()))
        .findFirst()
        .orElse(null);

    if (userStore == null) {
      throw new IllegalArgumentException("가게 정보를 찾을 수 없습니다.");
    }

    List<Category> categories = categoryRepository.findByCategoryNameIn(
        storeRequestDto.getCategories());

    List<StoreCategory> existingCategories = userStore.getStoreCategories();

    Set<String> existingCategoryNames = existingCategories.stream()
        .map(sc -> sc.getCategory().getCategoryName())
        .collect(Collectors.toSet());

    Set<String> newCategoryNames = categories.stream()
        .map(Category::getCategoryName)
        .collect(Collectors.toSet());

    List<StoreCategory> categoriesToRemove = existingCategories.stream()
        .filter(sc -> !newCategoryNames.contains(sc.getCategory().getCategoryName()))
        .collect(Collectors.toList());

    List<Category> categoriesToAdd = categories.stream()
        .filter(c -> !existingCategoryNames.contains(c.getCategoryName()))
        .collect(Collectors.toList());

    categoriesToRemove.forEach(userStore::removeStoreCategory);
    storeCategoryRepository.deleteAll(categoriesToRemove);

    List<StoreCategory> newStoreCategories = createStoreCategories(categoriesToAdd, userStore);
    newStoreCategories.forEach(userStore::addStoreCategory);

    userStore = Store.builder()
        .storeId(userStore.getStoreId())
        .storeName(storeRequestDto.getStoreName())
        .address(storeRequestDto.getAddress())
        .bRegiNum(storeRequestDto.getBRegiNum())
        .openAt(convertStringToTimestamp(storeRequestDto.getOpenAt()))
        .closeAt(convertStringToTimestamp(storeRequestDto.getCloseAt()))
        .user(userDetails.getUser())
        .storeCategories(userStore.getStoreCategories())
        .build();

    storeRepository.save(userStore);
  }


}