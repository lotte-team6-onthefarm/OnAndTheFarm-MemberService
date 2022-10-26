package com.team6.onandthefarmmemberservice.service.seller;

import com.team6.onandthefarmmemberservice.dto.seller.SellerDto;
import com.team6.onandthefarmmemberservice.security.jwt.Token;
import com.team6.onandthefarmmemberservice.vo.seller.SellerInfoResponse;

import java.io.IOException;

public interface SellerService {

    boolean updateByUserId(Long userId, SellerDto sellerDto) throws IOException;

    SellerInfoResponse findByUserId(Long userId);

    Boolean updatePassword(SellerDto sellerDto);

    boolean sellerSignup(SellerDto sellerDto);

    boolean sellerIdCheck(String sellerEmail);

    Token login(SellerDto sellerDto);

    Boolean searchSellerId(String sellerEmail, String phone);

}
