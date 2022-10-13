package com.team6.onandthefarmmemberservice.security.oauth.provider;

import com.team6.onandthefarmmemberservice.dto.user.UserLoginDto;
import com.team6.onandthefarmmemberservice.security.oauth.dto.OAuth2UserDto;
import org.springframework.stereotype.Component;

@Component
public interface OAuth2UserUtil {

	String getAccessToken(UserLoginDto userLoginDto);

	OAuth2UserDto getUserInfo(String accessToken);

}
