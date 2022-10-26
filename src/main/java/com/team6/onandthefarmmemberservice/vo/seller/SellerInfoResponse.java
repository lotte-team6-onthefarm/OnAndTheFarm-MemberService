package com.team6.onandthefarmmemberservice.vo.seller;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerInfoResponse {
    private String email;
    private String zipcode;
    private String address;
    private String addressDetail;
    private String phone;
    private String name;
    private String businessNumber;
    private String registerDate;
    private String sellerProfileImg;
}
