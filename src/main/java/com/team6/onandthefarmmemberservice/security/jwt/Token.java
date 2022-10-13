package com.team6.onandthefarmmemberservice.security.jwt;

import lombok.Getter;

@Getter
public class Token {

    private String token;
    private String refreshToken;

    public Token(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
