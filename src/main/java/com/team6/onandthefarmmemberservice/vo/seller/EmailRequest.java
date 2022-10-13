package com.team6.onandthefarmmemberservice.vo.seller;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ApiModel(description = "이메일 인증을 위한 객체")
public class EmailRequest {
    private String email;
}
