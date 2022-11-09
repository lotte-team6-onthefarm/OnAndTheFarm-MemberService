package com.team6.onandthefarmmemberservice.security.oauth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuth2UserDto {

    private String email;
    private String oauthId;

}
