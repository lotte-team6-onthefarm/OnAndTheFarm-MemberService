package com.team6.onandthefarmmemberservice.feignclient.controller;

import com.team6.onandthefarmmemberservice.feignclient.service.MemberServiceClientService;
import com.team6.onandthefarmmemberservice.feignclient.vo.UserClientResponse;
import com.team6.onandthefarmmemberservice.feignclient.vo.UserClientUserShortInfoResponse;
import com.team6.onandthefarmmemberservice.feignclient.vo.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberServiceClientController {

    private final MemberServiceClientService memberServiceClientService;

    /**
     * 유저ID를 이용해서 유저 정보를 가져오는 것
     * @param userId
     * @return
     */
    @GetMapping("/api/user/members/member-service/{user-no}")
    public UserVo findByUserId(@PathVariable("user-no") Long userId){
        return memberServiceClientService.findByUserId(userId);
    }

    @GetMapping("/api/user/member/member-service/short-info/{user-no}")
    UserClientUserShortInfoResponse findUserNameByUserId(@PathVariable("user-no") Long userId){
        return memberServiceClientService.getShortInfoResponse(userId);
    }
}
