package com.team6.onandthefarmmemberservice.dto.user;

import lombok.Data;

@Data
public class UserLoginDto {

    private String provider;

    private String code;

    private String state;

}
