package com.team6.onandthefarmmemberservice.feignclient.service;

import java.util.Optional;

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
		Optional<User> user = userRepository.findById(userId);
		if(user.isPresent()){
			UserClientUserShortInfoResponse userClientUserShortInfoResponse = UserClientUserShortInfoResponse.builder()
					.userProfileImg(user.get().getUserProfileImg())
					.userEmail(user.get().getUserEmail())
					.userName(user.get().getUserName())
					.build();
			return userClientUserShortInfoResponse;
		}
		return null;
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
