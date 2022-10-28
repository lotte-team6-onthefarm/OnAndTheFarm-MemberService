package com.team6.onandthefarmmemberservice.feignclient.controller;

import com.team6.onandthefarmmemberservice.feignclient.service.MemberServiceClientService;
import com.team6.onandthefarmmemberservice.feignclient.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/api/user/members/member-service/short-info/{user-no}")
    UserClientUserShortInfoResponse findUserNameByUserId(@PathVariable("user-no") Long userId){
        return memberServiceClientService.getShortInfoResponse(userId);
    }

    @GetMapping("/api/seller/members/member-service/{seller-no}")
    SellerVo findBySellerId(@PathVariable("seller-no")Long sellerId){
        return memberServiceClientService.findBySellerId(sellerId);
    }

    @GetMapping("/api/user/members/member-service/following")
    FollowingVo findByFollowingMemberIdAndFollowerMemberId(@RequestParam Long followingMemberId, @RequestParam Long followerMemberId){
        return memberServiceClientService.findByFollowingMemberIdAndFollowerMemberId(followingMemberId, followerMemberId);
    }

    @GetMapping("/api/user/members/member-service/following/list/{member-no}")
    List<FollowingVo> findByFollowingMemberId(@PathVariable("member-no")Long memberId){
        return memberServiceClientService.findByFollowingMemberId(memberId);
    }
}
