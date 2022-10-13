package com.team6.onandthefarmmemberservice.vo.seller;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@ApiModel(description = "셀러 비밀번호 변경을 위한 객체")
public class SellerPasswordRequest {
    private String email;
    private String password;

}
