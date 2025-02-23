package com.sparta.deliveryapp.user.service;


import com.sparta.deliveryapp.user.dto.MasterUserUpdateRequestDto;
import com.sparta.deliveryapp.user.dto.UserResponseDto;
import com.sparta.deliveryapp.user.entity.User;
import com.sparta.deliveryapp.user.repository.UserRepository;
import com.sparta.deliveryapp.util.NullAwareBeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MasterUserService {
  private final UserRepository userRepository;

  public List<UserResponseDto> getUsers() {
    List<UserResponseDto> users = userRepository.findAll().stream()
        .map(UserResponseDto::new).toList();

    return users;
  }

  public UserResponseDto updateUser(String email, MasterUserUpdateRequestDto requestDto) {
    User updateUser = userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("회원 정보가 없습니다,"));

    //TODO: 계속 서비스마다 null 체크 하지 않게 하기위해 entity에  @SQLRestriction() 추가
    if(updateUser.getDeletedAt() != null){
      throw new IllegalArgumentException("삭제가 된 회원입니다.");
    }


    NullAwareBeanUtils.copyNonNullProperties(requestDto, updateUser);

    // 업데이트된 사용자 저장
    userRepository.save(updateUser);

    return new UserResponseDto(updateUser);
  }
}
