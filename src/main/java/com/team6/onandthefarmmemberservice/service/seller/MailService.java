package com.team6.onandthefarmmemberservice.service.seller;

import com.team6.onandthefarmmemberservice.dto.seller.EmailDto;

import java.util.Map;


public interface MailService {

    void save(EmailDto emailDto);

    boolean checkAuthKey(Map<String, String> map) ;

    //인증메일 보내기
    public String sendAuthMail(String email) ;
}
