package com.team6.onandthefarmmemberservice.vo.seller;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@ApiModel(description = "셀러 정보 변경을 위한 객체")
public class SellerUpdateRequest {
    private String zipcode;
    private String address;
    private String addressDetail;
    private String shopName;
    private String password;
    private String phone;

}
