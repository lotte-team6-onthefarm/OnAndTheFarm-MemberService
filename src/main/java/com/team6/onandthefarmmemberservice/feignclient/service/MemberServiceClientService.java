package com.team6.onandthefarmmemberservice.feignclient.service;

import com.team6.onandthefarmmemberservice.feignclient.vo.UserClientResponse;

public interface MemberServiceClientService {
    UserClientResponse findByUserId(Long userId);
}
