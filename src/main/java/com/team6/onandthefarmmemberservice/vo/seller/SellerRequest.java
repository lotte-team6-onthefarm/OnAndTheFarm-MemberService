package com.team6.onandthefarmmemberservice.vo.seller;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@ApiModel(description = "셀러 상세 정보를 위한 객체")
public class SellerRequest {
    private String email;
    private String password;
    private String zipcode;
    private String address;
    private String addressDetail;
    private String phone;
    private String name;
    private String businessNumber;
    private String shopName;
}
