package com.team6.onandthefarmmemberservice.security.oauth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuth2UserDto {

    private String name;
    private String email;
    private Long kakaoId;
    private String naverId;

}
