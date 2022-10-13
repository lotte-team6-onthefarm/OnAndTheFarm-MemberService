package com.team6.onandthefarmmemberservice.vo.seller;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerRecentReviewResponse {
    private String reviewContent;

    private Integer reviewLikeCount;

    private Integer reviewRate;

    private String productImg;

    private String productName;
}
