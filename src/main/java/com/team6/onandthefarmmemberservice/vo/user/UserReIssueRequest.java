package com.team6.onandthefarmmemberservice.vo.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserReIssueRequest {

    private String accessToken;
    private String refreshToken;
}
