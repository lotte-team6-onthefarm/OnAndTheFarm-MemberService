package com.team6.onandthefarmmemberservice.feignclient.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.team6.onandthefarmmemberservice.feignclient.service.ProductServiceClientService;
import com.team6.onandthefarmmemberservice.feignclient.vo.SellerClientSellerDetailResponse;
import com.team6.onandthefarmmemberservice.feignclient.vo.UserClientUserShortInfo;


import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j

public class ProductServiceClientController {

	private final ProductServiceClientService productServiceClientService;

	public ProductServiceClientController(
			ProductServiceClientService productServiceClientService) {
		this.productServiceClientService = productServiceClientService;
	}

	@GetMapping("/api/user/member-service/username/{user-no}")
	public UserClientUserShortInfo findUserNameByUserId(@PathVariable("user-no") Long userId){
		return productServiceClientService.findUserNameByUserId(userId);
	}

	@GetMapping("/api/seller/member-service/seller/{seller-no}")
	public SellerClientSellerDetailResponse findBySellerId(@PathVariable("seller-no") Long sellerId){
		return productServiceClientService.findSellerDetailBySellerId(sellerId);
	}
}
