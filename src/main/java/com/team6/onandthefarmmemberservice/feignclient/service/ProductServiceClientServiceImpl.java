package com.team6.onandthefarmmemberservice.feignclient.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team6.onandthefarmmemberservice.entity.seller.Seller;
import com.team6.onandthefarmmemberservice.entity.user.User;
import com.team6.onandthefarmmemberservice.feignclient.vo.SellerClientSellerDetailResponse;
import com.team6.onandthefarmmemberservice.feignclient.vo.UserClientUserShortInfoResponse;
import com.team6.onandthefarmmemberservice.repository.SellerRepository;
import com.team6.onandthefarmmemberservice.repository.UserRepository;

@Service
@Transactional
public class ProductServiceClientServiceImpl implements ProductServiceClientService{

	private UserRepository userRepository;
	private SellerRepository sellerRepository;

	@Autowired
	public ProductServiceClientServiceImpl(UserRepository userRepository, SellerRepository sellerRepository){
		this.userRepository = userRepository;
		this.sellerRepository = sellerRepository;
	}

	public UserClientUserShortInfoResponse findUserNameByUserId(Long userId){
		User user = userRepository.findById(userId).get();

		UserClientUserShortInfoResponse userClientUserShortInfoResponse = UserClientUserShortInfoResponse.builder()
				.userProfileImg(user.getUserProfileImg())
				.userEmail(user.getUserEmail())
				.userName(user.getUserName())
				.build();
		return userClientUserShortInfoResponse;
	}

	public SellerClientSellerDetailResponse findSellerDetailBySellerId(Long sellerId){
		Seller seller = sellerRepository.findById(sellerId).get();

		SellerClientSellerDetailResponse sellerClientSellerDetailResponse = SellerClientSellerDetailResponse.builder()
				.sellerEmail(seller.getSellerEmail())
				.sellerAddress(seller.getSellerAddress())
				.sellerAddressDetail(seller.getSellerAddressDetail())
				.sellerPhone(seller.getSellerPhone())
				.sellerName(seller.getSellerName())
				.sellerShopName(seller.getSellerShopName())
				.sellerBusinessNumber(seller.getSellerBusinessNumber())
				.build();
		return sellerClientSellerDetailResponse;
	}
}
