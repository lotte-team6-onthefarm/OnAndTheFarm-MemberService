package com.team6.onandthefarmmemberservice.service.seller;

import com.team6.onandthefarmmemberservice.dto.seller.SellerDto;
import com.team6.onandthefarmmemberservice.dto.seller.SellerMypageDto;
import com.team6.onandthefarmmemberservice.security.jwt.Token;
import com.team6.onandthefarmmemberservice.vo.seller.SellerInfoResponse;
import com.team6.onandthefarmmemberservice.vo.seller.SellerMypageResponse;

import java.util.List;

public interface SellerService {
    boolean updateByUserId(Long userId, SellerDto sellerDto);
    SellerInfoResponse findByUserId(Long userId);
    Boolean updatePassword(SellerDto sellerDto);
    boolean sellerSignup(SellerDto sellerDto);
    boolean sellerIdCheck(String sellerEmail);
    Token login(SellerDto sellerDto);

//    SellerMypageResponse findSellerMypage(SellerMypageDto sellerMypageDto);
//
//    List<SellerProductQnaResponse> findSellerQnA(Long sellerId);
//
//    Boolean createQnaAnswer(SellerQnaDto sellerQnaDto);
}
