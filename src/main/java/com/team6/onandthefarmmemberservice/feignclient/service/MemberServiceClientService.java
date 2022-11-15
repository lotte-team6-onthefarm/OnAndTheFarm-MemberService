package com.team6.onandthefarmmemberservice.feignclient.service;

import com.team6.onandthefarmmemberservice.entity.user.ReservedPoint;
import com.team6.onandthefarmmemberservice.feignclient.vo.*;

import java.util.List;

public interface MemberServiceClientService {
    UserVo findByUserId(Long userId);
    UserClientUserShortInfoResponse getShortInfoResponse(Long userId);
    SellerVo findBySellerId(Long sellerId);

    FollowingVo findByFollowingMemberIdAndFollowerMemberId(Long followingMemberId, Long followerMemberId);

    ReservedPoint reservedPoint(String feedId, String orderSerial);

    Boolean confirmOrder(Long id);

    void cancelOrder(Long id);

    List<FollowingVo> findByFollowingMemberId(Long memberId);

    Boolean updateUserPoint(Long userId);
}
