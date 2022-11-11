package com.team6.onandthefarmmemberservice.dto.seller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SellerReIssueDto {
    private String accessToken;
    private String refreshToken;
}