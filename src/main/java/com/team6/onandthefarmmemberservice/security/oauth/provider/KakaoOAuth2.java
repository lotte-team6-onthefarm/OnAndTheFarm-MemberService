package com.team6.onandthefarmmemberservice.security.oauth.provider;

import com.team6.onandthefarmmemberservice.dto.user.UserLoginDto;
import com.team6.onandthefarmmemberservice.security.oauth.dto.OAuth2UserDto;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
public class KakaoOAuth2 {

	private Environment env;

	private String clientId;

	private String adminKey;

	private String redirectUrl;

	@Autowired
	public KakaoOAuth2(Environment env){
		this.env = env;
		this.clientId = env.getProperty("custom-api-key.kakao.client-id");
		this.adminKey = env.getProperty("custom-api-key.kakao.admin-key");
		this.redirectUrl = env.getProperty("custom-api-key.kakao.redirect-uri");
	}

	//@Override
//	public String getAuthCode() {
//		// HttpHeader 오브젝트 생성
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//		// HttpBody 오브젝트 생성
//		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//		params.add("response_type", "code");
//		params.add("client_id", clientId);
//		params.add("redirect_uri", redirectUrl);
//
//		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
//		RestTemplate rt = new RestTemplate();
//		HttpEntity<MultiValueMap<String, String>> kakaoAccessCodeRequest = new HttpEntity<>(params, headers);
//
//		try {
//			// Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
//			rt.exchange( "https://kauth.kakao.com/oauth/authorize", HttpMethod.GET, kakaoAccessCodeRequest, String.class );
//
//			return null;
//		} catch (HttpClientErrorException.BadRequest e){
//			log.error("getAuthCode - 잘못된 인가 코드");
//			return null;
//		}
//	}

	//@Override
	public String getAccessToken(UserLoginDto userLoginDto) {
		// HttpHeader 오브젝트 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HttpBody 오브젝트 생성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", clientId);
		params.add("redirect_uri", redirectUrl);
		params.add("code", userLoginDto.getCode());

		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		RestTemplate rt = new RestTemplate();
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

		try {
			// Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
			ResponseEntity<String> response = rt.exchange( "https://kauth.kakao.com/oauth/token", HttpMethod.POST, kakaoTokenRequest, String.class );
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

	//@Override
	public OAuth2UserDto getUserInfo(String accessToken) {
		// HttpHeader 오브젝트 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		RestTemplate rt = new RestTemplate();
		HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);

		try {
			// Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
			ResponseEntity<String> response = rt.exchange( "https://kapi.kakao.com/v2/user/me", HttpMethod.POST, kakaoUserInfoRequest, String.class );
			// JSON -> 액세스 토큰 파싱
			String userInfoString = response.getBody();
			JSONObject iJson = new JSONObject(userInfoString);
			Long kakaoId = iJson.getLong("id");
			//String name = iJson.getJSONObject("kakao_account").getString("name");
			String email = iJson.getJSONObject("kakao_account").getString("email");

			OAuth2UserDto userDto = OAuth2UserDto.builder()
					.kakaoId(kakaoId)
					.email(email)
					.build();

			return userDto;
		} catch (HttpClientErrorException.BadRequest e){
			log.error("getUserInfo - 잘못된 인가 코드");
			return null;
		}
	}

	public Long logout(Long userKakaoNumber) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		headers.add("Authorization", "KakaoAK " + adminKey);

		// HttpBody 오브젝트 생성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("target_id", String.valueOf(userKakaoNumber));
		params.add("target_id_type", "user_id");

		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		RestTemplate rt = new RestTemplate();
		HttpEntity<MultiValueMap<String, String>> kakaoAKRequest = new HttpEntity<>(params, headers);

		// Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
		ResponseEntity<String> response = rt.exchange("https://kapi.kakao.com/v1/user/logout", HttpMethod.POST, kakaoAKRequest, String.class);

		// 응답받은 id값 가져오기
		JSONObject body = new JSONObject(response.getBody());
		Long resKakaoId = body.getLong("id");

		return resKakaoId;
	}

	public Long unlinkUser(Long userKakaoNumber) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		headers.add("Authorization", "KakaoAK " + adminKey);

		// HttpBody 오브젝트 생성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("target_id", String.valueOf(userKakaoNumber));
		params.add("target_id_type", "user_id");

		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		RestTemplate rt = new RestTemplate();
		HttpEntity<MultiValueMap<String, String>> kakaoAKRequest = new HttpEntity<>(params, headers);
		ResponseEntity<String> response = rt.exchange("https://kapi.kakao.com/v1/user/unlink", HttpMethod.POST, kakaoAKRequest, String.class);

		// 응답받은 id값 추출
		JSONObject body = new JSONObject(response.getBody());
		Long resKakaoId = body.getLong("id");

		return resKakaoId;
	}

}
