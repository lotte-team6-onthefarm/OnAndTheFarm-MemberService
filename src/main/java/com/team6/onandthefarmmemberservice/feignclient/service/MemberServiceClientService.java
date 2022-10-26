package com.team6.onandthefarmmemberservice.feignclient.service;

import com.team6.onandthefarmmemberservice.feignclient.vo.UserClientResponse;
import com.team6.onandthefarmmemberservice.feignclient.vo.UserVo;

public interface MemberServiceClientService {
    UserVo findByUserId(Long userId);
}
