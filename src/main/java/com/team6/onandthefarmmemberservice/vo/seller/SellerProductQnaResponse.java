package com.team6.onandthefarmmemberservice.vo.seller;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerProductQnaResponse {
    private Long productQnaId;

    private String productQnaContent;

    private String productQnaCreatedAt;

    private String productQnaModifiedAt;

    private String productQnaStatus;

    private String productQnaCategory;

    private String userProfileImg;

    private String userName;

    private String productName;

    private String productImg;

    private String productSellerAnswer;

}
