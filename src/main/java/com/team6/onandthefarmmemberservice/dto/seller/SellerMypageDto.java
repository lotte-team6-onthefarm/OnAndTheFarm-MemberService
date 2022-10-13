package com.team6.onandthefarmmemberservice.dto.seller;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SellerMypageDto {
    private Long sellerId;

    private String startDate;

    private String endDate;
}
