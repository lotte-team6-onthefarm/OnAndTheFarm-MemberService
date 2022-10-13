package com.team6.onandthefarmmemberservice.vo.seller;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerMypageRequest {
    private String startDate;

    private String endDate;
}
