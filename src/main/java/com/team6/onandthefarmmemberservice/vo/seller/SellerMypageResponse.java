package com.team6.onandthefarmmemberservice.vo.seller;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerMypageResponse {
    private List<SellerRecentReviewResponse> reviews;

    private List<SellerPopularProductResponse> popularProducts;

    private List<Integer> dayPrices;

    private Integer totalPrice;
}
