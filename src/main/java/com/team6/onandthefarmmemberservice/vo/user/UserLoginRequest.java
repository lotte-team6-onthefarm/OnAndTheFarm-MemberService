package com.team6.onandthefarmmemberservice.vo.user;

import lombok.Data;

@Data
public class UserLoginRequest {

    private String provider;
    private String code;
    private String state;
}
