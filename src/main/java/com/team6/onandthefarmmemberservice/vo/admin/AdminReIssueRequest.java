package com.team6.onandthefarmmemberservice.vo.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminReIssueRequest {
    private String accessToken;
    private String refreshToken;
}