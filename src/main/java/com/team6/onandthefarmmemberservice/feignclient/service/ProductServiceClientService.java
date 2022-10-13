package com.team6.onandthefarmmemberservice.feignclient.service;

import com.team6.onandthefarmmemberservice.feignclient.vo.SellerClientSellerDetailResponse;
import com.team6.onandthefarmmemberservice.feignclient.vo.UserClientUserShortInfoResponse;

public interface ProductServiceClientService {
	UserClientUserShortInfoResponse findUserNameByUserId(Long userId);
	SellerClientSellerDetailResponse findSellerDetailBySellerId(Long sellerId);
}
