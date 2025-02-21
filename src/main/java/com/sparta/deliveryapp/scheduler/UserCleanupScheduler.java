package com.sparta.deliveryapp.scheduler;

import com.sparta.deliveryapp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class UserCleanupScheduler {
  private final UserService userService;

  // 매일 자정에 실행
  @Scheduled(cron = "0 0 0 * * ?")
  public void cleanupExpiredUsers() {
    log.info("soft delet 데이터 삭제 시작");
    userService.cleanupDeletedUsers();
  }
}
