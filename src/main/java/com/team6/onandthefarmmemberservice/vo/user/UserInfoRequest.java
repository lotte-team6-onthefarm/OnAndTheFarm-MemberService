package com.team6.onandthefarmmemberservice.vo.user;

import lombok.Data;

@Data
public class UserInfoRequest {

    private String userZipcode;

    private String userAddress;

    private String userAddressDetail;

    private String userPhone;

    private String userBirthday;

    private Integer userSex;

    private String userName;
}
