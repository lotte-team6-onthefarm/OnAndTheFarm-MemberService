package com.team6.onandthefarmmemberservice.service.seller;

import com.team6.onandthefarmmemberservice.dto.seller.SellerDto;
import com.team6.onandthefarmmemberservice.dto.seller.SellerReIssueDto;
import com.team6.onandthefarmmemberservice.vo.seller.SellerInfoResponse;
import com.team6.onandthefarmmemberservice.vo.seller.SellerLoginResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface SellerService {

    boolean updateByUserId(Long userId, SellerDto sellerDto) throws IOException;

    SellerInfoResponse findByUserId(Long userId);

    Boolean updatePassword(SellerDto sellerDto);

    boolean sellerSignup(SellerDto sellerDto);

    boolean sellerIdCheck(String sellerEmail);

    SellerLoginResponse login(SellerDto sellerDto);

    SellerLoginResponse reIssueToken(SellerReIssueDto sellerReIssueDto);

    Boolean logout(HttpServletRequest request);

    String searchSellerId(String name, String phone);

    Boolean searchSellerpasswd(String sellerEmail,String name);

}
