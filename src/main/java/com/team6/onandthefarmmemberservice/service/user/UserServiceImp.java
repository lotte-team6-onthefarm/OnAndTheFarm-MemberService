package com.team6.onandthefarmmemberservice.service.user;

import com.team6.onandthefarmmemberservice.dto.following.MemberFollowingDto;
import com.team6.onandthefarmmemberservice.dto.following.MemberProfileDto;
import com.team6.onandthefarmmemberservice.dto.user.UserInfoDto;
import com.team6.onandthefarmmemberservice.dto.user.UserLoginDto;
import com.team6.onandthefarmmemberservice.dto.user.UserReIssueDto;
import com.team6.onandthefarmmemberservice.entity.following.Following;
import com.team6.onandthefarmmemberservice.entity.seller.Seller;
import com.team6.onandthefarmmemberservice.entity.user.User;
import com.team6.onandthefarmmemberservice.repository.FollowingRepository;
import com.team6.onandthefarmmemberservice.repository.ReservedPointRepository;
import com.team6.onandthefarmmemberservice.repository.SellerRepository;
import com.team6.onandthefarmmemberservice.repository.UserRepository;
import com.team6.onandthefarmmemberservice.security.jwt.JwtTokenUtil;
import com.team6.onandthefarmmemberservice.security.jwt.Token;
import com.team6.onandthefarmmemberservice.security.oauth.dto.OAuth2UserDto;
import com.team6.onandthefarmmemberservice.security.oauth.provider.GoogleOAuth2;
import com.team6.onandthefarmmemberservice.security.oauth.provider.KakaoOAuth2;
import com.team6.onandthefarmmemberservice.security.oauth.provider.NaverOAuth2;
import com.team6.onandthefarmmemberservice.utils.DateUtils;
import com.team6.onandthefarmmemberservice.utils.RedisUtil;
import com.team6.onandthefarmmemberservice.utils.S3Upload;
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

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class UserServiceImp implements UserService {

	private final int pageContentNumber = 8;

	private Long refreshPeriod;

	private final UserRepository userRepository;

	private final SellerRepository sellerRepository;

	private final FollowingRepository followingRepository;

	private final KakaoOAuth2 kakaoOAuth2;
	private final NaverOAuth2 naverOAuth2;
	private final GoogleOAuth2 googleOAuth2;

	private final JwtTokenUtil jwtTokenUtil;

	private final RedisUtil redisUtil;

	private final DateUtils dateUtils;

	private final Environment env;

	private final S3Upload s3Upload;

	private final ReservedPointRepository reservedPointRepository;

	@Autowired
	public UserServiceImp(UserRepository userRepository,
						  SellerRepository sellerRepository,
						  FollowingRepository followingRepository,
						  DateUtils dateUtils,
						  Environment env,
						  KakaoOAuth2 kakaoOAuth2,
						  NaverOAuth2 naverOAuth2,
						  GoogleOAuth2 googleOAuth2,
						  JwtTokenUtil jwtTokenUtil,
						  RedisUtil redisUtil,
						  S3Upload s3Upload,
						  ReservedPointRepository reservedPointRepository) {
		this.userRepository = userRepository;
		this.sellerRepository = sellerRepository;
		this.followingRepository = followingRepository;
		this.dateUtils = dateUtils;
		this.env = env;
		this.kakaoOAuth2 = kakaoOAuth2;
		this.naverOAuth2 = naverOAuth2;
		this.googleOAuth2 = googleOAuth2;
		this.jwtTokenUtil = jwtTokenUtil;
		this.redisUtil = redisUtil;
		this.s3Upload=s3Upload;
		this.reservedPointRepository=reservedPointRepository;

		refreshPeriod = Long.parseLong(env.getProperty("custom-api-key.jwt.refresh-token-period"));
	}

	@Override
	public UserTokenResponse login(UserLoginDto userLoginDto) {

		OAuth2UserDto userInfo = null;
		String provider = userLoginDto.getProvider();
		if (provider.equals("google")) {
			String googleAccessToken = googleOAuth2.getAccessToken(userLoginDto);

			if (googleAccessToken != null) {
				userInfo = googleOAuth2.getUserInfo(googleAccessToken);
			}
		} else if (provider.equals("naver")) {
			String naverAccessToken = naverOAuth2.getAccessToken(userLoginDto);

			if (naverAccessToken != null) {
				userInfo = naverOAuth2.getUserInfo(naverAccessToken);
			}
		} else if (provider.equals("kakao")) {
			String kakaoAccessToken = kakaoOAuth2.getAccessToken(userLoginDto);

			if (kakaoAccessToken != null) {
				userInfo = kakaoOAuth2.getUserInfo(kakaoAccessToken);
			}
		}

		if(userInfo != null) {
			Optional<User> savedUser = userRepository.findByUserEmailAndProvider(userInfo.getEmail(), provider);
			User user = null;
			Boolean needRegister = false;
			String email = new String();

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
						.userOauthNumber(userInfo.getOauthId())
						.userFollowerCount(0)
						.userFollowingCount(0)
						.userProfileImg("https://lotte-06-s3-test.s3.ap-northeast-2.amazonaws.com/profile/user/basic_profile.png")
						.userRegisterDate(dateUtils.transDate(env.getProperty("dateutils.format")))
						.userIsActivated(true)
						.userPoint(0)
						.build();
				user = userRepository.save(newUser);
			}

			// jwt 토큰 발행
			Token token = jwtTokenUtil.generateToken(user.getUserId(), user.getRole());
			Long userId = user.getUserId();

			// redis에 refresh token 저장
			Duration expireDuration = Duration.ofMillis(refreshPeriod);
			redisUtil.setValueDuration(token.getRefreshToken(), Long.toString(userId), expireDuration);

			UserTokenResponse userTokenResponse = UserTokenResponse.builder()
					.token(token)
					.needRegister(needRegister)
					.email(email)
					.userId(userId)
					.build();

			return userTokenResponse;
		}
		return null;
	}

	@Override
	public UserTokenResponse reIssueToken(UserReIssueDto userReIssueDto) {
		// refresh token 가져오기
		String refreshToken = userReIssueDto.getRefreshToken();

		Long userId = Long.parseLong(redisUtil.getValues(refreshToken));
		Optional<User> savedUser = userRepository.findById(userId);
		if(savedUser.isPresent()){
			if(!jwtTokenUtil.checkExpiredToken(refreshToken)) {
				// Token 재생성
				Token newToken = jwtTokenUtil.generateToken(userId, savedUser.get().getRole());

				// 기존 refresh Token 삭제
				redisUtil.deleteValues(refreshToken);

				// 새 refresh token redis 저장
				Long newRefreshTokenExpiration = jwtTokenUtil.getTokenExpirationAsLong(newToken.getRefreshToken());
				Duration expireDuration = Duration.ofMillis(newRefreshTokenExpiration);
				redisUtil.setValueDuration(newToken.getRefreshToken(), Long.toString(userId), expireDuration);

				UserTokenResponse userTokenResponse = UserTokenResponse.builder()
						.token(newToken)
						.userId(savedUser.get().getUserId())
						.email(savedUser.get().getUserEmail())
						.needRegister(false)
						.build();

				if (savedUser.get().getUserName() == null) {
					userTokenResponse.setNeedRegister(true);
				}

				return userTokenResponse;
			}

		}
		return null;
	}

	@Override
	public Boolean logout(HttpServletRequest request, Long userId) {
		// Access, Refresh token 처리
		Boolean completeDel = deleteToken(request);
		if (!completeDel) {
			return false;
		}

		Optional<User> user = userRepository.findById(userId);

		String provider = user.get().getProvider();
		if(provider.equals("kakao")) {
			Long kakaoNumber = Long.parseLong(user.get().getUserOauthNumber());
			kakaoOAuth2.logout(kakaoNumber);
		}

		return true;
	}

	// 로그아웃 시 토큰 처리
	private Boolean deleteToken(HttpServletRequest request) {
		try {
			String accessToken = request.getHeader("Authorization");
			String refreshToken = request.getHeader("refresh");

			// 기존의 refresh Token을 삭제 -> 해당 refresh Token으로 토큰 재발행하지 못하도록
			redisUtil.deleteValues(refreshToken);

			// access Token 블랙리스트 추가
			Integer tokenExpiration = jwtTokenUtil.getTokenExpirationAsLong(accessToken).intValue();
			Duration expireDuration = Duration.ofMillis(tokenExpiration);
			redisUtil.setValueDuration(accessToken, "BlackList", expireDuration);

		} catch (Exception e) {
			log.error(String.valueOf(e));
			return false;
		}
		return true;
	}

	@Override
	public Boolean loginPhoneConfirm(String phone){
		Optional<User> user = userRepository.findByUserPhone(phone);

		if(user.isPresent()){
			return false;
		}

		return true;
	}

	@Override
	public Long updateUserInfo(UserInfoDto userInfoDto) throws IOException {
		Optional<User> user = userRepository.findById(userInfoDto.getUserId());

		if(userInfoDto.getProfile()!=null){
			String url = s3Upload.profileUserUpload(userInfoDto.getProfile());
			user.get().setUserProfileImg(url);
		}

		user.get().setUserName(userInfoDto.getUserName());
		user.get().setUserPhone(userInfoDto.getUserPhone());
		user.get().setUserZipcode(userInfoDto.getUserZipcode());
		user.get().setUserAddress(userInfoDto.getUserAddress());
		user.get().setUserAddressDetail(userInfoDto.getUserAddressDetail());
		user.get().setUserBirthday(userInfoDto.getUserBirthday());
		user.get().setUserSex(userInfoDto.getUserSex());


		return user.get().getUserId();
	}

	@Override
	public UserInfoResponse findUserInfo(Long userId) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		Optional<User> user = userRepository.findById(userId);

		UserInfoResponse response = modelMapper.map(user.get(), UserInfoResponse.class);

		return response;
	}

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
	public MemberFollowResult getFollowerList(MemberFollowerListRequest memberFollowerListRequest){

		Long memberId = memberFollowerListRequest.getMemberId();
		Long loginMemberId = memberFollowerListRequest.getLoginMemberId();
		String loginMemberRole = memberFollowerListRequest.getLoginMemberRole();
		List<Following> followerList = followingRepository.findFollowingIdByFollowerId(memberId);

		int startIndex = memberFollowerListRequest.getPageNumber() * pageContentNumber;
		int size = followerList.size();

		MemberFollowResult memberFollowResult = getResponseForFollower(size, startIndex, followerList, loginMemberId, loginMemberRole);
		memberFollowResult.setCurrentPageNum(memberFollowerListRequest.getPageNumber());
		memberFollowResult.setTotalElementNum(size);
		if(size%pageContentNumber==0){
			memberFollowResult.setTotalPageNum(size/pageContentNumber);
		}
		else{
			memberFollowResult.setTotalPageNum((size/pageContentNumber)+1);
		}

		return memberFollowResult;
	}

	@Override
	public MemberFollowResult getFollowingList(MemberFollowingListRequest memberFollowingListRequest){

		Long memberId = memberFollowingListRequest.getMemberId();
		Long loginMemberId = memberFollowingListRequest.getLoginMemberId();
		String loginMemberRole = memberFollowingListRequest.getLoginMemberRole();
		List<Following> followingList = followingRepository.findFollowerIdByFollowingId(memberId);

		int startIndex = memberFollowingListRequest.getPageNumber() * pageContentNumber;
		int size = followingList.size();

		MemberFollowResult memberFollowResult = getResponseForFollowing(size, startIndex, followingList, loginMemberId, loginMemberRole);
		memberFollowResult.setCurrentPageNum(memberFollowingListRequest.getPageNumber());
		memberFollowResult.setTotalElementNum(size);
		if(size%pageContentNumber==0){
			memberFollowResult.setTotalPageNum(size/pageContentNumber);
		}
		else{
			memberFollowResult.setTotalPageNum((size/pageContentNumber)+1);
		}

		return memberFollowResult;
	}

	@Override
	public MemberProfileResponse getMemberProfile(MemberProfileDto memberProfileDto){
		Long memberId = memberProfileDto.getMemberId();
		String memberRole = memberProfileDto.getMemberRole();

		MemberProfileResponse memberProfileResponse = null;
		if(memberRole.equals("user")){
			User user = userRepository.findById(memberId).get();

			String userName = user.getUserName();
			String userProfileImage = user.getUserProfileImg();

			memberProfileResponse = MemberProfileResponse.builder()
					.memberRole(memberRole)
					.memberName(userName)
					.memberProfileImage(userProfileImage)
					.followingCount(user.getUserFollowingCount())
					.followerCount(user.getUserFollowerCount())
					.build();
		}
		else if (memberRole.equals("seller")){
			Seller seller = sellerRepository.findById(memberId).get();
			memberProfileResponse = MemberProfileResponse.builder()
					.memberRole(memberRole)
					.memberName(seller.getSellerName())
					.memberProfileImage(seller.getSellerProfileImg())
					.followingCount(seller.getSellerFollowingCount())
					.followerCount(seller.getSellerFollowerCount())
					.build();
		}

		memberProfileResponse.setFollowStatus(false);
		if(memberId.equals(memberProfileDto.getLoginMemberId())){
			memberProfileResponse.setIsModifiable(true);
		}
		else{
			memberProfileResponse.setIsModifiable(false);
			Optional<Following> following = followingRepository.findByFollowingMemberIdAndFollowerMemberId(memberProfileDto.getLoginMemberId(), memberId);
			if(following.isPresent()){
				memberProfileResponse.setFollowStatus(true);
			}
		}

		return memberProfileResponse;
	}

	@Override
	public Boolean isAlreadyProcessedOrderId(String orderSerial) {
		boolean result // 처리된 메시지가 있으면 true / 없으면 false
				= reservedPointRepository.existsByOrderSerialAndIdempoStatus(orderSerial,true);

		if(!result){ // 처리된 메시지가 없는 경우 중복되지 않은 메시지
			return true; //
		}

		return false;
	}

	@Override
	public Boolean addPoint(String orderSerial, Long memberId) {
		Optional<User> user = userRepository.findById(memberId);
		if(user.isPresent()){
			Integer userPoint = user.get().getUserPoint();
			user.get().setUserPoint(userPoint+10);
			reservedPointRepository.findByOrderSerial(orderSerial).get().setIdempoStatus(true);
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public MemberFollowResult getResponseForFollower(int size, int startIndex, List<Following> followerList, Long loginMemberId, String loginMemberRole){
		MemberFollowResult memberFollowResult = new MemberFollowResult();
		List<MemberFollowListResponse> responseList = new ArrayList<>();
		if(size < startIndex){
			memberFollowResult.setMemberFollowListResponseList(responseList);
			return memberFollowResult;
		}

		if(size < startIndex + pageContentNumber) {
			for (Following following : followerList.subList(startIndex, size)) {
				Long followingMemberId = following.getFollowingMemberId();
				String followingMemberRole = following.getFollowingMemberRole();

				MemberFollowListResponse memberFollowListResponse = new MemberFollowListResponse();
				if(followingMemberRole.equals("user")){
					User user = userRepository.findById(followingMemberId).get();
					memberFollowListResponse.setMemberId(user.getUserId());
					memberFollowListResponse.setMemberRole("user");
					memberFollowListResponse.setMemberName(user.getUserName());
					memberFollowListResponse.setMemberImg(user.getUserProfileImg());
					responseList.add(memberFollowListResponse);
				}

				else {
					Seller seller = sellerRepository.findById(followingMemberId).get();
					memberFollowListResponse.setMemberId(seller.getSellerId());
					memberFollowListResponse.setMemberRole("seller");
					memberFollowListResponse.setMemberName(seller.getSellerName());
					memberFollowListResponse.setMemberImg(seller.getSellerProfileImg());
					responseList.add(memberFollowListResponse);
				}

				memberFollowListResponse.setIsModifiable(false);
				if(followingMemberId.equals(loginMemberId)){
					memberFollowListResponse.setIsModifiable(true);
				}

				memberFollowListResponse.setFollowStatus(false);
				Optional<Following> followingStatus = followingRepository.findByFollowingMemberIdAndFollowerMemberId(loginMemberId, followingMemberId);
				if(followingStatus.isPresent()){
					memberFollowListResponse.setFollowStatus(true);
				}
			}

			memberFollowResult.setMemberFollowListResponseList(responseList);
			return memberFollowResult;
		}

		for (Following following : followerList.subList(startIndex, startIndex+pageContentNumber)) {
			Long followingMemberId = following.getFollowingMemberId();
			String followingMemberRole = following.getFollowingMemberRole();

			MemberFollowListResponse memberFollowListResponse = new MemberFollowListResponse();
			if(followingMemberRole.equals("user")){
				User user = userRepository.findById(followingMemberId).get();
				memberFollowListResponse.setMemberId(user.getUserId());
				memberFollowListResponse.setMemberRole("user");
				memberFollowListResponse.setMemberName(user.getUserName());
				memberFollowListResponse.setMemberImg(user.getUserProfileImg());
				responseList.add(memberFollowListResponse);
			}

			else {
				Seller seller = sellerRepository.findById(followingMemberId).get();
				memberFollowListResponse.setMemberId(seller.getSellerId());
				memberFollowListResponse.setMemberRole("seller");
				memberFollowListResponse.setMemberName(seller.getSellerName());
				memberFollowListResponse.setMemberImg(seller.getSellerProfileImg());
				responseList.add(memberFollowListResponse);
			}

			memberFollowListResponse.setIsModifiable(false);
			if(followingMemberId.equals(loginMemberId)){
				memberFollowListResponse.setIsModifiable(true);
			}

			memberFollowListResponse.setFollowStatus(false);
			Optional<Following> followingStatus = followingRepository.findByFollowingMemberIdAndFollowerMemberId(loginMemberId, followingMemberId);
			if(followingStatus.isPresent()){
				memberFollowListResponse.setFollowStatus(true);
			}
		}

		memberFollowResult.setMemberFollowListResponseList(responseList);
		return memberFollowResult;
	}

	public MemberFollowResult getResponseForFollowing(int size, int startIndex, List<Following> followingList, Long loginMemberId, String loginMemberRole){
		MemberFollowResult memberFollowResult = new MemberFollowResult();
		List<MemberFollowListResponse> responseList = new ArrayList<>();
		if(size < startIndex){
			memberFollowResult.setMemberFollowListResponseList(responseList);
			return memberFollowResult;
		}

		if(size < startIndex + pageContentNumber) {
			for (Following following : followingList.subList(startIndex, size)) {
				Long followerMemberId = following.getFollowerMemberId();
				String followerMemberRole = following.getFollowerMemberRole();

				MemberFollowListResponse memberFollowListResponse = new MemberFollowListResponse();
				if(followerMemberRole.equals("user")){
					User user = userRepository.findById(followerMemberId).get();
					memberFollowListResponse.setMemberId(user.getUserId());
					memberFollowListResponse.setMemberRole("user");
					memberFollowListResponse.setMemberName(user.getUserName());
					memberFollowListResponse.setMemberImg(user.getUserProfileImg());
					responseList.add(memberFollowListResponse);
				}

				else {
					Seller seller = sellerRepository.findById(followerMemberId).get();
					memberFollowListResponse.setMemberId(seller.getSellerId());
					memberFollowListResponse.setMemberRole("seller");
					memberFollowListResponse.setMemberName(seller.getSellerName());
					memberFollowListResponse.setMemberImg(seller.getSellerProfileImg());
					responseList.add(memberFollowListResponse);
				}

				memberFollowListResponse.setIsModifiable(false);
				if(followerMemberId.equals(loginMemberId)){
					memberFollowListResponse.setIsModifiable(true);
				}

				memberFollowListResponse.setFollowStatus(false);
				Optional<Following> followingStatus = followingRepository.findByFollowingMemberIdAndFollowerMemberId(loginMemberId, followerMemberId);
				if(followingStatus.isPresent()){
					memberFollowListResponse.setFollowStatus(true);
				}
			}

			memberFollowResult.setMemberFollowListResponseList(responseList);
			return memberFollowResult;
		}

		for (Following following : followingList.subList(startIndex, startIndex+pageContentNumber)) {
			Long followerMemberId = following.getFollowerMemberId();
			String followerMemberRole = following.getFollowerMemberRole();

			MemberFollowListResponse memberFollowListResponse = new MemberFollowListResponse();
			if(followerMemberRole.equals("user")){
				User user = userRepository.findById(followerMemberId).get();
				memberFollowListResponse.setMemberId(user.getUserId());
				memberFollowListResponse.setMemberRole("user");
				memberFollowListResponse.setMemberName(user.getUserName());
				memberFollowListResponse.setMemberImg(user.getUserProfileImg());
				responseList.add(memberFollowListResponse);
			}

			else {
				Seller seller = sellerRepository.findById(followerMemberId).get();
				memberFollowListResponse.setMemberId(seller.getSellerId());
				memberFollowListResponse.setMemberRole("seller");
				memberFollowListResponse.setMemberName(seller.getSellerName());
				memberFollowListResponse.setMemberImg(seller.getSellerProfileImg());
				responseList.add(memberFollowListResponse);
			}

			memberFollowListResponse.setIsModifiable(false);
			if(followerMemberId.equals(loginMemberId)){
				memberFollowListResponse.setIsModifiable(true);
			}

			memberFollowListResponse.setFollowStatus(false);
			Optional<Following> followingStatus = followingRepository.findByFollowingMemberIdAndFollowerMemberId(loginMemberId, followerMemberId);
			if(followingStatus.isPresent()){
				memberFollowListResponse.setFollowStatus(true);
			}
		}

		memberFollowResult.setMemberFollowListResponseList(responseList);
		return memberFollowResult;
	}

}
