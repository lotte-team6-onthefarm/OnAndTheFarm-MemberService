package com.team6.onandthefarmmemberservice.feignclient.service;

import com.team6.onandthefarmmemberservice.feignclient.vo.SellerVo;
import com.team6.onandthefarmmemberservice.feignclient.vo.UserClientResponse;
import com.team6.onandthefarmmemberservice.feignclient.vo.UserClientUserShortInfoResponse;
import com.team6.onandthefarmmemberservice.feignclient.vo.UserVo;

public interface MemberServiceClientService {
    UserVo findByUserId(Long userId);
    UserClientUserShortInfoResponse getShortInfoResponse(Long userId);
    SellerVo findBySellerId(Long sellerId);
}
