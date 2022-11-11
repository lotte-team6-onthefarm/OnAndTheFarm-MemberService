package com.team6.onandthefarmmemberservice.vo.seller;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SellerReIssueRequest {
    private String accessToken;
    private String refreshToken;
}