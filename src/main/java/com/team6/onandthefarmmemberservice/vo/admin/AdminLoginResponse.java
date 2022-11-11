package com.team6.onandthefarmmemberservice.vo.admin;

import com.team6.onandthefarmmemberservice.security.jwt.Token;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminLoginResponse {

    private Token token;
    private String role;
}