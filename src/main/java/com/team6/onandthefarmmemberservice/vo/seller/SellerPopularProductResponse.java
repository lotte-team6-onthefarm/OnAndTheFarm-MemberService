package com.team6.onandthefarmmemberservice.vo.seller;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerPopularProductResponse {
    private String productImg;

    private String productName;

    private Integer productWishCount;
}
