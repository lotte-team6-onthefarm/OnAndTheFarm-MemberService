package com.team6.onandthefarmmemberservice.security.oauth.provider;

import com.team6.onandthefarmmemberservice.dto.user.UserLoginDto;
import com.team6.onandthefarmmemberservice.security.oauth.dto.OAuth2UserDto;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Component
public class GoogleOAuth2 {
	private Environment env;
	private String clientId;
	private String clientSecret;
	private String redirectUrl;


	public GoogleOAuth2(Environment env){
		this.env = env;
		this.clientId = env.getProperty("custom-api-key.google.client-id");
		this.clientSecret = env.getProperty("custom-api-key.google.client-secret");
		this.redirectUrl = env.getProperty("custom-api-key.google.redirect-uri");
	}

	public String getAccessToken(UserLoginDto userLoginDto) {
		// HttpHeader 오브젝트 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded");

		String code = userLoginDto.getCode().replace("%2F", "/");

		// HttpBody 오브젝트 생성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("redirect_uri", redirectUrl);
		params.add("code", code);

		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		RestTemplate rt = new RestTemplate();
		HttpEntity<MultiValueMap<String, String>> googleTokenRequest = new HttpEntity<>(params, headers);

		try {
			// Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
			ResponseEntity<String> response = rt.exchange( "https://oauth2.googleapis.com/token", HttpMethod.POST, googleTokenRequest, String.class);

			// JSON -> 액세스 토큰 파싱
			String tokenJson = response.getBody();
			JSONObject rjson = new JSONObject(tokenJson);
			String accessToken = rjson.getString("access_token");

			return accessToken;
		} catch (HttpClientErrorException.BadRequest e){
			log.error("getAccessToken - 잘못된 인가 코드");
			return null;
		}
	}

	public OAuth2UserDto getUserInfo(String accessToken) {
		// HttpHeader 오브젝트 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);

		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		RestTemplate rt = new RestTemplate();
		HttpEntity<MultiValueMap<String, String>> googleUserInfoRequest = new HttpEntity<>(headers);

		try {
			// Http 요청하기 - Get방식으로 - 그리고 response 변수의 응답 받음.
			ResponseEntity<String> response = rt.exchange( "https://www.googleapis.com/oauth2/v2/userinfo", HttpMethod.GET, googleUserInfoRequest, String.class );
			// JSON -> 액세스 토큰 파싱
			String userInfoString = response.getBody();
			JSONObject iJson = new JSONObject(userInfoString);
			String googleId = iJson.getString("id");
			String email = iJson.getString("email");

			OAuth2UserDto userDto = OAuth2UserDto.builder()
					.oauthId(googleId)
					.email(email)
					.build();

			return userDto;
		} catch (HttpClientErrorException.BadRequest e){
			log.error("getUserInfo - 잘못된 인가 코드");
			return null;
		}
	}
}
