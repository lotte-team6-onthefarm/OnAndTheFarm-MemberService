package com.team6.onandthefarmmemberservice.feignclient.service;

import com.team6.onandthefarmmemberservice.feignclient.vo.SellerClientSellerDetailResponse;
import com.team6.onandthefarmmemberservice.feignclient.vo.UserClientUserShortInfo;

public interface ProductServiceClientService {
	UserClientUserShortInfo findUserNameByUserId(Long userId);
	SellerClientSellerDetailResponse findSellerDetailBySellerId(Long sellerId);
}
