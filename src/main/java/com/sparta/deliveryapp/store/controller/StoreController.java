package com.sparta.deliveryapp.store.controller;

import com.sparta.deliveryapp.store.dto.StoreRequestDto;
import com.sparta.deliveryapp.store.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.Store;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores/")
public class StoreController {
    private final StoreService storeService;

    @PostMapping("/regi")
    public ResponseEntity<String> regiStore(@Valid @RequestBody StoreRequestDto storeRequestDto,
        BindingResult bindingResult) {
        //bindingresult check 로직 작성하기
        //owner 권한 체크하기
        storeService.regiStore(storeRequestDto);

        return ResponseEntity.ok().body("가게가 정상적으로 등록되었습니다.");
    }

}