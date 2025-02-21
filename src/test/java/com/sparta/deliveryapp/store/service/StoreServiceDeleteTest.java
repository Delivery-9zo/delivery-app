package com.sparta.deliveryapp.store.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sparta.deliveryapp.store.entity.Store;
import com.sparta.deliveryapp.store.repository.StoreRepository;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
public class StoreServiceDeleteTest {

  @InjectMocks
  private StoreService storeService;

  @Mock
  private StoreRepository storeRepository;

  @Mock
  private UserDetailsImpl userDetails;

  @Test
  void deleteStore_WithMasterRole_ShouldDeleteSuccessfully() {
    // given
    String storeId = UUID.randomUUID().toString();

    Store store = mock(Store.class);

    Mockito.doReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_MASTER")));
    Mockito.when(storeRepository.findByStoreId(UUID.fromString(storeId)))
        .thenReturn(Optional.of(store));

    // when
    storeService.deleteStore(storeId);

    // then
    verify(store).onPreRemove();
    verify(storeRepository).save(store);
  }

  @Test
  void deleteStore_WithoutMasterRole_ShouldThrowAuthorizationDeniedException() {
    // given
    String storeId = UUID.randomUUID().toString();

    Mockito.doReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")))
        .when(userDetails).getAuthorities();

    // when then
    assertThrows(AuthorizationDeniedException.class,
        () -> storeService.deleteStore(storeId));
  }

  @Test
  void deleteStore_WithNonExistentStore_ShouldThrowEntityNotFoundException() {
    // given
    String storeId = UUID.randomUUID().toString();

    Mockito.doReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_MASTER")));

    Mockito.when(storeRepository.findByStoreId(UUID.fromString(storeId)))
        .thenReturn(Optional.empty());

    // when then
    assertThrows(EntityNotFoundException.class,
        () -> storeService.deleteStore(storeId));
  }
}

