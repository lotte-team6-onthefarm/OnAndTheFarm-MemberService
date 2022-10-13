package com.team6.onandthefarmmemberservice.security.oauth.provider;

import com.team6.onandthefarmmemberservice.dto.user.UserLoginDto;
import com.team6.onandthefarmmemberservice.security.oauth.dto.OAuth2UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class GoogleOAuth2 implements OAuth2UserUtil {


	@Override
	public String getAccessToken(UserLoginDto userLoginDto) {
		return null;
	}

	@Override
	public OAuth2UserDto getUserInfo(String accessToken) {
		return null;
	}
}
