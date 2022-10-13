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
public class NaverOAuth2 implements OAuth2UserUtil {

    private Environment env;

    private String clientId;

    private String clientSecret;

    private String redirectUrl;

    public NaverOAuth2(Environment env){
        this.env = env;
        this.clientId = env.getProperty("custom-api-key.naver.client-id");
        this.clientSecret = env.getProperty("custom-api-key.naver.client-secret");
        this.redirectUrl = env.getProperty("custom-api-key.naver.redirect-uri");
    }

    @Override
    public String getAccessToken(UserLoginDto userLoginDto) {
        // HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", userLoginDto.getCode());
        params.add("state", userLoginDto.getState());

        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> naverTokenRequest = new HttpEntity<>(params, headers);

        try {
            // Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
            ResponseEntity<String> response = rt.exchange( "https://nid.naver.com/oauth2.0/token", HttpMethod.POST, naverTokenRequest, String.class );
            // JSON -> 액세스 토큰 파싱
            String tokenJson = response.getBody();
            JSONObject rjson = new JSONObject(tokenJson);
            String accessToken = rjson.get("access_token").toString();
            //String refreshToken = rjson.getString("refresh_token");
            //String tokenType = rjson.getString("token_type");

            return accessToken;
        } catch (HttpClientErrorException.BadRequest e){
            log.error("getAccessToken - 잘못된 인가 코드");
            return null;
        }
    }

    @Override
    public OAuth2UserDto getUserInfo(String accessToken) {
        // HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.add("Authorization", "Bearer "+accessToken);

        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> naverTokenRequest = new HttpEntity<>(headers);

        try {
            // Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
            ResponseEntity<String> response = rt.exchange( "https://openapi.naver.com/v1/nid/me", HttpMethod.POST, naverTokenRequest, String.class );
            // JSON -> 액세스 토큰 파싱
            String userInfoString = response.getBody();
            JSONObject iJson = new JSONObject(userInfoString);
            JSONObject responseJson = new JSONObject(iJson.get("response").toString());
            String naverId = responseJson.getString("id");
            String name = responseJson.getString("name");
            String email = responseJson.getString("email");

            OAuth2UserDto userDto = OAuth2UserDto.builder()
                    .naverId(naverId)
                    .name(name)
                    .email(email)
                    .build();

            return userDto;
        } catch (HttpClientErrorException.BadRequest e){
            log.error("getAccessToken - 잘못된 인가 코드");
            return null;
        }
    }
}
