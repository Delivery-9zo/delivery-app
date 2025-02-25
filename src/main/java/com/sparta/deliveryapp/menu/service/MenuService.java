package com.sparta.deliveryapp.menu.service;

import com.sparta.deliveryapp.menu.dto.MenuGetResponseDto;
import com.sparta.deliveryapp.menu.dto.MenuPatchRequestDto;
import com.sparta.deliveryapp.menu.dto.MenuPostRequestDto;
import com.sparta.deliveryapp.menu.entity.Menu;
import com.sparta.deliveryapp.menu.repository.MenuRepository;
import com.sparta.deliveryapp.store.entity.Store;
import com.sparta.deliveryapp.store.repository.StoreRepository;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {

  private final MenuRepository menuRepository;
  private final StoreRepository storeRepository;
  private final EntityManager entityManager;

  @Transactional
  public void postMenu(MenuPostRequestDto dto, UserDetailsImpl userDetails) {
    Store store = storeRepository.findById(dto.storeId()).orElseThrow(
        () -> new IllegalArgumentException("가게를 찾을 수 없습니다.")
    );

    checkStoreAccessPermission(userDetails, store);

    Menu menu = new Menu();
    menu.setStore(store);
    menu.setName(dto.name());
    menu.setInfo(dto.info());
    menu.setPrice(dto.price());
    menu.setImage(dto.image());

    menuRepository.save(menu);
  }

  @Transactional
  public MenuGetResponseDto getMenu(String name, UUID storeId) {
    Menu menu = menuRepository.findByNameAndStore_StoreId(name, storeId).orElseThrow(
        () -> new IllegalArgumentException("메뉴를 찾을 수 없습니다.")
    );

    return new MenuGetResponseDto(
        menu.getId(),
        storeId,
        menu.getName(),
        menu.getInfo(),
        menu.getPrice(),
        menu.getImage()
    );
  }

  @Transactional
  public Page<MenuGetResponseDto> getMenus(UUID storeId, Pageable pageable) {
    Page<Menu> menus = menuRepository.findAllByStore_StoreId(storeId, pageable);

    return menus.map(menu -> new MenuGetResponseDto(
        menu.getId(),
        storeId,
        menu.getName(),
        menu.getInfo(),
        menu.getPrice(),
        menu.getImage()
    ));
  }

  @Transactional
  public void patchMenu(String menuName, UUID storeId, MenuPatchRequestDto dto,
      UserDetailsImpl userDetails) {
    Menu menu = menuRepository.findByNameAndStore_StoreId(menuName, storeId).orElseThrow(
        () -> new IllegalArgumentException("메뉴를 찾을 수 없습니다.")
    );

    Store store = storeRepository.findById(storeId).orElseThrow(
        () -> new IllegalArgumentException("가게를 찾을 수 없습니다.")
    );

    checkStoreAccessPermission(userDetails, store);

    if (dto.name() != null) {
      menu.setName(dto.name());
      log.info("메뉴 이름 수정 {} -> {}", menuName, dto.name());
    }
    if (dto.info() != null) {
      menu.setInfo(dto.info());
      log.info("메뉴 정보 수정 {} -> {}", menu.getInfo(), dto.info());
    }
    if (dto.price() != null) {
      menu.setPrice(dto.price());
      log.info("메뉴 가격 수정 {} -> {}", menu.getPrice(), dto.price());
    }
    if (dto.image() != null) {
      menu.setImage(dto.image());
      log.info("메뉴 이미지 수정");
    }

    // TODO: NullAwareBeanUtils 를 이용한 리펙터링
  }

  @Transactional
  public void deleteMenuById(UUID menuId, UUID storeId, UserDetailsImpl userDetails) {
    Store store = storeRepository.findById(storeId).orElseThrow(
        () -> new IllegalArgumentException("가게를 찾을 수 없습니다.")
    );

    checkStoreAccessPermission(userDetails, store);

    entityManager.createNativeQuery(
            "UPDATE p_menu SET deleted_at = current_timestamp, deleted_by = ? WHERE menu_uuid = ?"
        ).setParameter(1, userDetails.getEmail())
        .setParameter(2, menuId)
        .executeUpdate();

  }

  @Transactional
  public void deleteMenu(UUID storeId, UserDetailsImpl userDetails) {
    Store store = storeRepository.findById(storeId).orElseThrow(
        () -> new IllegalArgumentException("가게를 찾을 수 없습니다.")
    );

    checkStoreAccessPermission(userDetails, store);

    menuRepository.softDeleteMenu(storeId, userDetails.getEmail());
  }


  private void checkStoreAccessPermission(UserDetailsImpl userDetails, Store store) {
    if (store.isNotAssociated(userDetails.getUser())) {
      throw new IllegalArgumentException("이 상점에 접근 권한이 없는 사용자입니다.");
    }
  }


}
