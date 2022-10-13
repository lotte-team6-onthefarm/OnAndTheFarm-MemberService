package com.team6.onandthefarmmemberservice.service.user;


import com.team6.onandthefarmmemberservice.dto.following.MemberFollowingDto;
import com.team6.onandthefarmmemberservice.dto.user.UserInfoDto;
import com.team6.onandthefarmmemberservice.dto.user.UserLoginDto;
import com.team6.onandthefarmmemberservice.entity.following.Following;
import com.team6.onandthefarmmemberservice.entity.seller.Seller;
import com.team6.onandthefarmmemberservice.entity.user.User;
import com.team6.onandthefarmmemberservice.repository.FollowingRepository;
import com.team6.onandthefarmmemberservice.repository.SellerRepository;
import com.team6.onandthefarmmemberservice.repository.UserRepository;
import com.team6.onandthefarmmemberservice.security.jwt.JwtTokenUtil;
import com.team6.onandthefarmmemberservice.security.jwt.Token;
import com.team6.onandthefarmmemberservice.security.oauth.dto.OAuth2UserDto;
import com.team6.onandthefarmmemberservice.security.oauth.provider.KakaoOAuth2;
import com.team6.onandthefarmmemberservice.security.oauth.provider.NaverOAuth2;
import com.team6.onandthefarmmemberservice.utils.DateUtils;
import com.team6.onandthefarmmemberservice.vo.following.*;
import com.team6.onandthefarmmemberservice.vo.user.UserInfoResponse;
import com.team6.onandthefarmmemberservice.vo.user.UserTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class UserServiceImp implements UserService {

	private final UserRepository userRepository;

	private final SellerRepository sellerRepository;

	//private final ProductQnaRepository productQnaRepository;

	//private final ProductQnaAnswerRepository productQnaAnswerRepository;

	//private final ProductRepository productRepository;

	private final FollowingRepository followingRepository;

	private final KakaoOAuth2 kakaoOAuth2;
	private final NaverOAuth2 naverOAuth2;

	private final JwtTokenUtil jwtTokenUtil;

	private final DateUtils dateUtils;

	private final Environment env;

	@Autowired
	public UserServiceImp(UserRepository userRepository,
			SellerRepository sellerRepository,
			FollowingRepository followingRepository,
			DateUtils dateUtils,
			Environment env,
			KakaoOAuth2 kakaoOAuth2,
			NaverOAuth2 naverOAuth2,
			JwtTokenUtil jwtTokenUtil)
		  //ProductQnaRepository productQnaRepository,
		  //ProductQnaAnswerRepository productQnaAnswerRepository,
		  //ProductRepository productRepository)
		{
		this.userRepository = userRepository;
		this.sellerRepository = sellerRepository;
		this.followingRepository = followingRepository;
		this.dateUtils = dateUtils;
		this.env = env;
		this.kakaoOAuth2 = kakaoOAuth2;
		this.naverOAuth2 = naverOAuth2;
		this.jwtTokenUtil = jwtTokenUtil;
//		this.productQnaRepository = productQnaRepository;
//		this.productQnaAnswerRepository=productQnaAnswerRepository;
//		this.productRepository = productRepository;

	}



	@Override
	public UserTokenResponse login(UserLoginDto userLoginDto) {

		Token token = null;
		Boolean needRegister = false;
		String email = new String();

		String provider = userLoginDto.getProvider();
		if (provider.equals("google")) {

		} else if (provider.equals("naver")) {
			// 카카오 액세스 토큰 받아오기
			String naverAccessToken = naverOAuth2.getAccessToken(userLoginDto);

			if (naverAccessToken != null) {
				// 카카오 액세스 토큰으로 유저 정보 받아오기
				OAuth2UserDto userInfo = naverOAuth2.getUserInfo(naverAccessToken);

				Optional<User> savedUser = userRepository.findByUserEmailAndProvider(userInfo.getEmail(), provider);
				User user = null;

				if (savedUser.isPresent()) {
					user = savedUser.get();

					if (user.getUserName() == null) {
						needRegister = true;
						email = user.getUserEmail();
					}
				} else { // DB에 유저 정보가 없다면 저장
					needRegister = true; // 유저 정보 추가 등록이 필요함
					email = userInfo.getEmail();

					User newUser = User.builder()
							.userEmail(userInfo.getEmail())
							.role("ROLE_USER")
							.provider(provider)
							.userNaverNumber(userInfo.getNaverId())
							.build();
					user = userRepository.save(newUser);
				}

				// jwt 토큰 발행
				token = jwtTokenUtil.generateToken(user.getUserId(), user.getRole());
			}
		} else if (provider.equals("kakao")) {
			// 카카오 액세스 토큰 받아오기
			String kakaoAccessToken = kakaoOAuth2.getAccessToken(userLoginDto);

			if (kakaoAccessToken != null) {
				// 카카오 액세스 토큰으로 유저 정보 받아오기
				OAuth2UserDto userInfo = kakaoOAuth2.getUserInfo(kakaoAccessToken);

				Optional<User> savedUser = userRepository.findByUserEmailAndProvider(userInfo.getEmail(), provider);

				User user = null;
				if (savedUser.isPresent()) {
					user = savedUser.get();

					if (user.getUserName() == null) {
						needRegister = true;
						email = user.getUserEmail();
					}
				} else { // DB에 유저 정보가 없다면 저장
					needRegister = true; // 유저 정보 추가 등록이 필요함
					email = userInfo.getEmail();

					User newUser = User.builder()
							.userEmail(userInfo.getEmail())
							.role("ROLE_USER")
							.provider(provider)
							.userKakaoNumber(userInfo.getKakaoId())
							.build();
					user = userRepository.save(newUser);
				}

				// jwt 토큰 발행
				token = jwtTokenUtil.generateToken(user.getUserId(), user.getRole());
			}
		}
		UserTokenResponse userTokenResponse = UserTokenResponse.builder()
				.token(token)
				.needRegister(needRegister)
				.email(email)
				.build();

		return userTokenResponse;
	}

	@Override
	public Boolean logout(Long userId) {
		Optional<User> user = userRepository.findById(userId);

		Long kakaoNumber = user.get().getUserKakaoNumber();
		Long returnKakaoNumber = kakaoOAuth2.logout(kakaoNumber);
		if (returnKakaoNumber == null) {
			return false;
		}

		return true;
	}

	@Override
	public Long registerUserInfo(UserInfoDto userInfoDto) {
		Optional<User> user = userRepository.findById(userInfoDto.getUserId());

		user.get().setUserName(userInfoDto.getUserName());
		user.get().setUserPhone(userInfoDto.getUserPhone());
		user.get().setUserZipcode(userInfoDto.getUserZipcode());
		user.get().setUserAddress(userInfoDto.getUserAddress());
		user.get().setUserAddressDetail(userInfoDto.getUserAddressDetail());
		user.get().setUserBirthday(userInfoDto.getUserBirthday());
		user.get().setUserSex(userInfoDto.getUserSex());
		user.get().setUserFollowerCount(0);
		user.get().setUserFollowingCount(0);

		return user.get().getUserId();
	}

	@Override
	public Token reIssueToken(String refreshToken, HttpServletRequest request, HttpServletResponse response) {
		return null;
	}

	@Override
	public Long updateUserInfo(UserInfoDto userInfoDto) {
		Optional<User> user = userRepository.findById(userInfoDto.getUserId());

		user.get().setUserName(userInfoDto.getUserName());
		user.get().setUserPhone(userInfoDto.getUserPhone());
		user.get().setUserZipcode(userInfoDto.getUserZipcode());
		user.get().setUserAddress(userInfoDto.getUserAddress());
		user.get().setUserAddressDetail(userInfoDto.getUserAddressDetail());
		user.get().setUserBirthday(userInfoDto.getUserBirthday());
		user.get().setUserSex(userInfoDto.getUserSex());

		return user.get().getUserId();
	}

	public UserInfoResponse findUserInfo(Long userId) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		Optional<User> user = userRepository.findById(userId);

		UserInfoResponse response = modelMapper.map(user.get(), UserInfoResponse.class);

		return response;
	}

//	public Boolean createProductQnA(UserQnaDto userQnaDto) {
//		Optional<User> user = userRepository.findById(userQnaDto.getUserId());
//		Optional<Product> product = productRepository.findById(userQnaDto.getProductId());
//		log.info("product 정보  :  " + product.get().toString());
//		ProductQna productQna = ProductQna.builder()
//				.product(product.get())
//				.user(user.get())
//				.productQnaContent(userQnaDto.getProductQnaContent())
//				.productQnaCreatedAt(dateUtils.transDate(env.getProperty("dateutils.format")))
//				.productQnaStatus("waiting")
//				.seller(product.get().getSeller())
//				.build();
//		ProductQna newQna = productQnaRepository.save(productQna);
//		if (newQna == null) {
//			return Boolean.FALSE;
//		}
//		return Boolean.TRUE;
//	}
//
//	public List<ProductQnAResponse> findUserQna(Long userId) {
//		ModelMapper modelMapper = new ModelMapper();
//		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//
//		List<ProductQnAResponse> responses = new ArrayList<>();
//
//		Optional<User> user = userRepository.findById(userId);
//		if (user.isPresent()) {
//			List<ProductQna> productQnas = productQnaRepository.findByUser(user.get());
//
//			for (ProductQna productQna : productQnas) {
//				ProductQnAResponse response = modelMapper.map(productQna, ProductQnAResponse.class);
//				if(response.getProductQnaStatus().equals("completed")){
//					String answer =
//							productQnaAnswerRepository
//									.findByProductQna(productQna)
//									.getProductQnaAnswerContent();
//					response.setProductSellerAnswer(answer);
//				}
//				responses.add(response);
//			}
//		}
//
//		return responses;
//	}
//
//	/**
//	 * 유저의 질의를 수정하는 메서드
//	 * @param userQnaUpdateDto
//	 * @return
//	 */
//	public Boolean updateUserQna(UserQnaUpdateDto userQnaUpdateDto) {
//		Optional<ProductQna> productQna = productQnaRepository.findById(userQnaUpdateDto.getProductQnaId());
//		productQna.get().setProductQnaContent(userQnaUpdateDto.getProductQnaContent());
//		productQna.get().setProductQnaModifiedAt(dateUtils.transDate(env.getProperty("dateutils.format")));
//		if (productQna.get().getProductQnaContent().equals(userQnaUpdateDto.getProductQnaContent())) {
//			return Boolean.TRUE;
//		}
//		return Boolean.FALSE;
//	}
//
//	public Boolean deleteUserQna(Long productQnaId) {
//		Optional<ProductQna> productQna = productQnaRepository.findById(productQnaId);
//		productQna.get().setProductQnaStatus("deleted");
//		if (productQna.get().getProductQnaStatus().equals("deleted")) {
//			return Boolean.TRUE;
//		}
//		return Boolean.FALSE;
//	}

	@Override
	public Long addFollowList(MemberFollowingDto memberFollowingDto) {
		Long followingMemberId = memberFollowingDto.getFollowingMemberId();
		Long followerMemberId = memberFollowingDto.getFollowerMemberId();
		String followingMemberRole = memberFollowingDto.getFollowingMemberRole();
		String followerMemberRole = memberFollowingDto.getFollowerMemberRole();

		Optional<Following> savedFollowing = followingRepository.findByFollowingMemberIdAndFollowerMemberId(
				followingMemberId, followerMemberId);

		if (savedFollowing.isPresent()) {
			return savedFollowing.get().getFollowingId();
		}

		if (followingMemberRole.equals("user") && followerMemberRole.equals("user")) {
			User followingMember = userRepository.findById(followingMemberId).get();
			User followerMember = userRepository.findById(followerMemberId).get();

			followingMember.setUserFollowingCount(followingMember.getUserFollowingCount() + 1);
			followerMember.setUserFollowerCount(followerMember.getUserFollowerCount() + 1);
		} else if (followingMemberRole.equals("user") && followerMemberRole.equals("seller")) {
			User followingMember = userRepository.findById(followingMemberId).get();
			Seller followerMember = sellerRepository.findById(followerMemberId).get();

			followingMember.setUserFollowingCount(followingMember.getUserFollowingCount() + 1);
			followerMember.setSellerFollowerCount(followerMember.getSellerFollowerCount() + 1);
		} else if (followingMemberRole.equals("seller") && followerMemberRole.equals("user")) {
			Seller followingMember = sellerRepository.findById(followingMemberId).get();
			User followerMember = userRepository.findById(followerMemberId).get();

			followingMember.setSellerFollowingCount(followingMember.getSellerFollowingCount() + 1);
			followerMember.setUserFollowerCount(followerMember.getUserFollowerCount() + 1);
		} else if (followingMemberRole.equals("seller") && followerMemberRole.equals("seller")) {
			Seller followingMember = sellerRepository.findById(followingMemberId).get();
			Seller followerMember = sellerRepository.findById(followerMemberId).get();

			followingMember.setSellerFollowingCount(followingMember.getSellerFollowingCount() + 1);
			followerMember.setSellerFollowerCount(followingMember.getSellerFollowerCount() + 1);
		}

		Following following = Following.builder()
				.followingMemberId(followingMemberId)
				.followingMemberRole(followingMemberRole)
				.followerMemberId(followerMemberId)
				.followerMemberRole(followerMemberRole)
				.build();
		Long followingId = followingRepository.save(following).getFollowingId();

		return followingId;
	}

	@Override
	public Long cancelFollowList(MemberFollowingDto memberFollowingDto) {
		Long followingCancelMemberId = memberFollowingDto.getFollowingMemberId();
		Long followerCancelMemberId = memberFollowingDto.getFollowerMemberId();
		String followingCancelMemberRole = memberFollowingDto.getFollowingMemberRole();
		String followerCancelMemberRole = memberFollowingDto.getFollowerMemberRole();

		if (followingCancelMemberRole.equals("user") && followerCancelMemberRole.equals("user")) {
			User followingMember = userRepository.findById(followingCancelMemberId).get();
			User followerMember = userRepository.findById(followerCancelMemberId).get();

			followingMember.setUserFollowingCount(followingMember.getUserFollowingCount() - 1);
			followerMember.setUserFollowerCount(followerMember.getUserFollowerCount() - 1);
		} else if (followingCancelMemberRole.equals("user") && followerCancelMemberRole.equals("seller")) {
			User followingMember = userRepository.findById(followingCancelMemberId).get();
			Seller followerMember = sellerRepository.findById(followerCancelMemberId).get();

			followingMember.setUserFollowingCount(followingMember.getUserFollowingCount() - 1);
			followerMember.setSellerFollowerCount(followerMember.getSellerFollowerCount() - 1);
		} else if (followingCancelMemberRole.equals("seller") && followerCancelMemberRole.equals("user")) {
			Seller followingMember = sellerRepository.findById(followingCancelMemberId).get();
			User followerMember = userRepository.findById(followerCancelMemberId).get();

			followingMember.setSellerFollowingCount(followingMember.getSellerFollowingCount() - 1);
			followerMember.setUserFollowerCount(followerMember.getUserFollowerCount() - 1);
		} else if (followingCancelMemberRole.equals("seller") && followerCancelMemberRole.equals("seller")) {
			Seller followingMember = sellerRepository.findById(followingCancelMemberId).get();
			Seller followerMember = sellerRepository.findById(followerCancelMemberId).get();

			followingMember.setSellerFollowingCount(followingMember.getSellerFollowingCount() - 1);
			followerMember.setSellerFollowerCount(followingMember.getSellerFollowerCount() - 1);
		}

		Following following = followingRepository.findByFollowingMemberIdAndFollowerMemberId(
				followingCancelMemberId, followerCancelMemberId).get();
		Long followingId = following.getFollowingId();
		followingRepository.delete(following);

		return followingId;
	}

	@Override
	public MemberFollowCountResponse getFollowingCount(MemberFollowCountRequest memberFollowCountRequest) {
		User user;
		Seller seller;
		MemberFollowCountResponse memberFollowCountResponse = null;
		Long memberId = memberFollowCountRequest.getMemberId();
		String memberRole = memberFollowCountRequest.getMemberRole();

		if (memberRole.equals("user")) {
			user = userRepository.findById(memberId).get();

			memberFollowCountResponse = MemberFollowCountResponse.builder().
					memberId(user.getUserId())
					.followingCount(user.getUserFollowingCount())
					.followerCount(user.getUserFollowerCount()).
					build();
			
		} else if (memberRole.equals("seller")) {
			seller = sellerRepository.findById(memberId).get();

			memberFollowCountResponse = MemberFollowCountResponse.builder().
					memberId(seller.getSellerId())
					.followingCount(seller.getSellerFollowingCount())
					.followerCount(seller.getSellerFollowerCount()).
					build();
		}
		
		return memberFollowCountResponse;
	}

	@Override
	public List<MemberFollowerListResponse> getFollowerList(MemberFollowerListRequest memberFollowerListRequest){
		User user;
		Seller seller;
		Long memberId = memberFollowerListRequest.getMemberId();
		List<MemberFollowerListResponse> followerResponseList = new ArrayList<>();
		List<Following> followerList = followingRepository.findFollowingIdByFollowerId(memberId);

			for (Following following : followerList) {
				Long followingMemberId = following.getFollowingMemberId();
				String followingMemberRole = following.getFollowingMemberRole();
				if(followingMemberRole.equals("user")){
					user = userRepository.findById(followingMemberId).get();
					MemberFollowerListResponse memberFollowerListResponse = MemberFollowerListResponse.builder()
							.memberId(user.getUserId())
							.memberName(user.getUserName())
							.memberImg(user.getUserProfileImg())
							.build();
					followerResponseList.add(memberFollowerListResponse);
				}

				else {
					seller = sellerRepository.findById(followingMemberId).get();
					MemberFollowerListResponse memberFollowerListResponse = MemberFollowerListResponse.builder()
							.memberId(seller.getSellerId())
							.memberName(seller.getSellerName())
							.memberImg(seller.getSellerProfileImg())
							.build();
					followerResponseList.add(memberFollowerListResponse);
				}
			}
		return followerResponseList;
	}

	@Override
	public List<MemberFollowingListResponse> getFollowingList(MemberFollowingListRequest memberFollowingListRequest){
		User user;
		Seller seller;
		Long memberId = memberFollowingListRequest.getMemberId();
		List<MemberFollowingListResponse> followingResponseList = new ArrayList<>();
		List<Following> followingList = followingRepository.findFollowerIdByFollowingId(memberId);

		for (Following following : followingList) {
			Long followingMemberId = following.getFollowingMemberId();
			String followingMemberRole = following.getFollowingMemberRole();
			if(followingMemberRole.equals("user")){
				user = userRepository.findById(followingMemberId).get();
				MemberFollowingListResponse memberFollowingListResponse = MemberFollowingListResponse.builder()
						.memberId(user.getUserId())
						.memberName(user.getUserName())
						.memberImg(user.getUserProfileImg())
						.build();
				followingResponseList.add(memberFollowingListResponse);
			}

			else {
				seller = sellerRepository.findById(followingMemberId).get();
				MemberFollowingListResponse memberFollowingListResponse = MemberFollowingListResponse.builder()
						.memberId(seller.getSellerId())
						.memberName(seller.getSellerName())
						.memberImg(seller.getSellerProfileImg())
						.build();
				followingResponseList.add(memberFollowingListResponse);
			}
		}
		return followingResponseList;
	}
}
