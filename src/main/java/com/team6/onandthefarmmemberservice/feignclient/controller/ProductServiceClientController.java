package com.team6.onandthefarmmemberservice.feignclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.team6.onandthefarmmemberservice.feignclient.service.ProductServiceClientService;
import com.team6.onandthefarmmemberservice.feignclient.vo.SellerClientSellerDetailResponse;
import com.team6.onandthefarmmemberservice.feignclient.vo.UserClientUserShortInfoResponse;


import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ProductServiceClientController {

	private final ProductServiceClientService productServiceClientService;

	@Autowired
	public ProductServiceClientController(
			ProductServiceClientService productServiceClientService) {
		this.productServiceClientService = productServiceClientService;
	}

	@GetMapping("/api/user/members/member-service/username/{user-no}")
	public UserClientUserShortInfoResponse findUserNameByUserId(@PathVariable("user-no") Long userId){
		return productServiceClientService.findUserNameByUserId(userId);
	}

	@GetMapping("/api/seller/members/member-service/seller-detail/{seller-no}")
	public SellerClientSellerDetailResponse findBySellerId(@PathVariable("seller-no") Long sellerId){
		return productServiceClientService.findSellerDetailBySellerId(sellerId);
	}
}
