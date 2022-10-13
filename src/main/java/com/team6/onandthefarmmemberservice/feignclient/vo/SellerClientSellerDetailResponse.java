package com.team6.onandthefarmmemberservice.feignclient.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class SellerClientSellerDetailResponse {
	private Long sellerId;
	private String sellerEmail;
	private String sellerAddress;
	private String sellerAddressDetail;
	private String sellerPhone;
	private String sellerName;
	private String sellerShopName;
	private String sellerBusinessNumber;
}
