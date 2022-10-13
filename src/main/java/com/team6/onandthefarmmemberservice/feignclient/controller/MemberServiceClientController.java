package com.team6.onandthefarmmemberservice.feignclient.controller;

import com.team6.onandthefarmmemberservice.feignclient.vo.UserClientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberServiceClientController {

    private final 

    /**
     * 유저ID를 이용해서 유저 정보를 가져오는 것
     * @param userId
     * @return
     */
    @GetMapping("/api/user/member-service/user/{user-no}")
    public UserClientResponse findByUserId(@PathVariable("user-no") Long userId){

    }
}
