package com.team6.onandthefarmmemberservice.controller;

import com.team6.onandthefarmmemberservice.dto.following.MemberFollowingDto;
import com.team6.onandthefarmmemberservice.dto.user.UserInfoDto;
import com.team6.onandthefarmmemberservice.dto.user.UserLoginDto;
import com.team6.onandthefarmmemberservice.entity.following.Following;
import com.team6.onandthefarmmemberservice.service.user.UserService;
import com.team6.onandthefarmmemberservice.utils.BaseResponse;
import com.team6.onandthefarmmemberservice.vo.following.*;
import com.team6.onandthefarmmemberservice.vo.user.UserInfoRequest;
import com.team6.onandthefarmmemberservice.vo.user.UserInfoResponse;
import com.team6.onandthefarmmemberservice.vo.user.UserLoginRequest;
import com.team6.onandthefarmmemberservice.vo.user.UserTokenResponse;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    //private final ProductService productService;

    @Autowired
    public UserController(UserService userService
                          //ProductService productService
    ) {
        this.userService = userService;
        //this.productService = productService;
    }

    @PostMapping("/login")
    @ApiOperation(value = "유저 소셜 로그인")
    public ResponseEntity<BaseResponse<UserTokenResponse>> login(@RequestBody UserLoginRequest userLoginRequest) {

        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setProvider(userLoginRequest.getProvider());
        userLoginDto.setCode(userLoginRequest.getCode());
        userLoginDto.setState(userLoginRequest.getState());

        UserTokenResponse userTokenResponse = userService.login(userLoginDto);

        BaseResponse response = null;
        if (userTokenResponse.getToken() != null) {
            response = BaseResponse.builder().httpStatus(HttpStatus.OK).message("성공").data(userTokenResponse).build();
        } else {
            log.error("oauth 접근 토큰 발급 실패");
            BaseResponse badResponse = BaseResponse.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("잘못된 로그인 요청입니다.")
                    .build();
            return new ResponseEntity<>(badResponse, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping("/logout")
    @ApiOperation(value = "유저 로그아웃")
    public ResponseEntity<BaseResponse> logout(@ApiIgnore Principal principal) {

        Long userId = Long.parseLong(principal.getName());
        userService.logout(userId);

        BaseResponse response = BaseResponse.builder().httpStatus(HttpStatus.OK).message("성공").build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/register")
    @ApiOperation(value = "소셜 로그인 후 유저의 추가 정보 저장")
    public ResponseEntity<BaseResponse> join(@ApiIgnore Principal principal,
            @RequestBody UserInfoRequest userInfoRequest) {

        BaseResponse response = null;

        UserInfoDto userInfoDto = UserInfoDto.builder()
                .userId(Long.parseLong(principal.getName()))
                .userZipcode(userInfoRequest.getUserZipcode())
                .userAddress(userInfoRequest.getUserAddress())
                .userAddressDetail(userInfoRequest.getUserAddressDetail())
                .userName(userInfoRequest.getUserName())
                .userBirthday(userInfoRequest.getUserBirthday())
                .userPhone(userInfoRequest.getUserPhone())
                .userSex(userInfoRequest.getUserSex())
                .build();

        Long userId = userService.registerUserInfo(userInfoDto);
        if (userId != -1L) {
            response = BaseResponse.builder().httpStatus(HttpStatus.OK).message("성공").build();
        } else {
            response = BaseResponse.builder().httpStatus(HttpStatus.FORBIDDEN).message("실패").build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/update")
    @ApiOperation(value = "유저 정보 수정")
    public ResponseEntity<BaseResponse> updateUserInfo(@ApiIgnore Principal principal,
            @RequestBody UserInfoRequest userInfoRequest) {

        BaseResponse response = null;

        UserInfoDto userInfoDto = UserInfoDto.builder()
                .userId(Long.parseLong(principal.getName()))
                .userZipcode(userInfoRequest.getUserZipcode())
                .userAddress(userInfoRequest.getUserAddress())
                .userAddressDetail(userInfoRequest.getUserAddressDetail())
                .userPhone(userInfoRequest.getUserPhone())
                .userBirthday(userInfoRequest.getUserBirthday())
                .userName(userInfoRequest.getUserName())
                .userSex(userInfoRequest.getUserSex())
                .build();

        Long userId = userService.updateUserInfo(userInfoDto);
        if (userId != -1L) {
            response = BaseResponse.builder().httpStatus(HttpStatus.OK).message("성공").build();
        } else {
            response = BaseResponse.builder().httpStatus(HttpStatus.FORBIDDEN).message("실패").build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/mypage/info")
    @ApiOperation(value = "유저 정보 조회")
    public ResponseEntity<BaseResponse<UserInfoResponse>> findUserInfo(@ApiIgnore Principal principal) {
        UserInfoResponse userInfoResponse = userService.findUserInfo(Long.valueOf(principal.getName()));
        BaseResponse response = BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("OK")
                .data(userInfoResponse)
                .build();
        return new ResponseEntity(response, HttpStatus.OK);
    }

    //product service 에 추가해야할 부분
//    @GetMapping("/mypage/wish")
//    @ApiOperation(value = "사용자 별 위시리스트 조회")
//    public ResponseEntity<BaseResponse<List<ProductWishResponse>>> getWishList(@ApiIgnore Principal principal) {
//
//        Long userId = Long.parseLong(principal.getName());
//        List<ProductWishResponse> productInfos = productService.getWishList(userId);
//
//        BaseResponse baseResponse = BaseResponse.builder()
//                .httpStatus(HttpStatus.CREATED)
//                .message("get wish list by user completed")
//                .data(productInfos)
//                .build();
//
//        return new ResponseEntity(baseResponse, HttpStatus.OK);
//    }
//
//    @GetMapping("/mypage/review")
//    @ApiOperation(value = "사용자 별로 작성 가능한 리뷰 조회")
//    public ResponseEntity<BaseResponse<List<ProductReviewResponse>>> getWritableReviewList(
//            @ApiIgnore Principal principal) {
//
//        Long userId = Long.parseLong(principal.getName());
//
//        List<ProductReviewResponse> productsWithoutReview = productService.getProductsWithoutReview(userId);
//
//        BaseResponse baseResponse = BaseResponse.builder()
//                .httpStatus(HttpStatus.OK)
//                .message("get writable reviews completed")
//                .data(productsWithoutReview)
//                .build();
//
//        return new ResponseEntity(baseResponse, HttpStatus.OK);
//    }
//
//    @PostMapping("/QnA")
//    @ApiOperation(value = "유저 질의 생성")
//    public ResponseEntity<BaseResponse> createQnA(@ApiIgnore Principal principal,
//            @RequestBody UserQnaRequest userQnaRequest) {
//
//        ModelMapper modelMapper = new ModelMapper();
//        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//
//        Long userId = Long.parseLong(principal.getName());
//        UserQnaDto userQnaDto = modelMapper.map(userQnaRequest, UserQnaDto.class);
//        userQnaDto.setUserId(userId);
//
//        Boolean result = userService.createProductQnA(userQnaDto);
//        BaseResponse response = BaseResponse.builder()
//                .httpStatus(HttpStatus.OK)
//                .message("OK")
//                .data(result)
//                .build();
//        return new ResponseEntity(response, HttpStatus.CREATED);
//    }
//
//    @GetMapping("/mypage/QnA")
//    @ApiOperation(value = "유저 질의 조회")
//    public ResponseEntity<BaseResponse<List<ProductQnAResponse>>> findUserQnA(@ApiIgnore Principal principal) {
//
//        ModelMapper modelMapper = new ModelMapper();
//        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//
//        Long userId = Long.parseLong(principal.getName());
//
//        List<ProductQnAResponse> responses = userService.findUserQna(userId);
//        BaseResponse response = BaseResponse.builder()
//                .httpStatus(HttpStatus.OK)
//                .message("유저 QNA 조회")
//                .data(responses)
//                .build();
//        return new ResponseEntity(response, HttpStatus.CREATED);
//    }
//
//    @PutMapping("/QnA")
//    @ApiOperation(value = "유저 질의 수정")
//    public ResponseEntity<BaseResponse<Boolean>> updateUserQnA(
//            @ApiIgnore Principal principal, @RequestBody UserQnaUpdateRequest userQnaUpdateRequest) {
//        ModelMapper modelMapper = new ModelMapper();
//        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//        UserQnaUpdateDto userQnaUpdateDto = modelMapper.map(userQnaUpdateRequest, UserQnaUpdateDto.class);
//        userQnaUpdateDto.setUserId(Long.valueOf(principal.getName()));
//        Boolean result = userService.updateUserQna(userQnaUpdateDto);
//        BaseResponse response = BaseResponse.builder()
//                .httpStatus(HttpStatus.OK)
//                .message("OK")
//                .data(result)
//                .build();
//        return new ResponseEntity(response, HttpStatus.OK);
//    }
//
//    @PutMapping("/QnA/delete")
//    @ApiOperation(value = "유저 질의 삭제")
//    public ResponseEntity<BaseResponse<Boolean>> deleteUserQnA(@RequestParam Long productQnaId) {
//        Boolean result = userService.deleteUserQna(productQnaId);
//
//        BaseResponse response = BaseResponse.builder()
//                .httpStatus(HttpStatus.OK)
//                .message("OK")
//                .data(result)
//                .build();
//
//        return new ResponseEntity(response, HttpStatus.OK);
//    }

    @PostMapping("/follow/add")
    @ApiOperation(value = "다른 유저 팔로우")
    public ResponseEntity<BaseResponse<Following>> addFollow(@ApiIgnore Principal principal,
                                                             @RequestBody MemberFollowRequest memberFollowRequest) {

        MemberFollowingDto memberFollowingDto = MemberFollowingDto.builder()
                .followingMemberId(Long.parseLong(principal.getName()))
                .followingMemberRole("user")
                .followerMemberId(memberFollowRequest.getFollowerMemberId())
                .followerMemberRole(memberFollowRequest.getFollowerMemberRole())
                .build();

        Long followingId = userService.addFollowList(memberFollowingDto);

        BaseResponse response = BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("OK")
                .data(followingId)
                .build();

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PutMapping("/follow/cancel")
    @ApiOperation(value = "다른 유저 팔로우 취소")
    public ResponseEntity<BaseResponse<Following>> cancelFollow(@ApiIgnore Principal principal,
            @RequestBody MemberFollowRequest memberFollowRequest) {

        MemberFollowingDto memberFollowingDto = MemberFollowingDto.builder()
                .followingMemberId(Long.parseLong(principal.getName()))
                .followingMemberRole("user")
                .followerMemberId(memberFollowRequest.getFollowerMemberId())
                .followerMemberRole(memberFollowRequest.getFollowerMemberRole())
                .build();
        Long followingId = userService.cancelFollowList(memberFollowingDto);

        BaseResponse response = BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("OK")
                .data(followingId)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("follow/count")
    @ApiOperation(value = "멤버의 팔로잉/팔로워 수 조회")
    public ResponseEntity<BaseResponse<MemberFollowCountResponse>> getFollowCount(
            @RequestBody MemberFollowCountRequest memberFollowCountRequest) {

        MemberFollowCountResponse followCount = userService.getFollowingCount(memberFollowCountRequest);

        BaseResponse response = BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("OK")
                .data(followCount)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("follow/follower-list")
    @ApiOperation(value = "멤버의 팔로워 유저 리스트 조회")
    public ResponseEntity<BaseResponse<List<MemberFollowerListResponse>>> getFollowerList(
            @RequestBody MemberFollowerListRequest memberFollowerListRequest){
        List<MemberFollowerListResponse> followerList = userService.getFollowerList(memberFollowerListRequest);

        BaseResponse response = BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("OK")
                .data(followerList)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("follow/following-list")
    @ApiOperation(value = "멤버의 팔로워 유저 리스트 조회")
    public ResponseEntity<BaseResponse<List<MemberFollowingListResponse>>> getFollowingList(
            @RequestBody MemberFollowingListRequest memberFollowingListRequest){
        List<MemberFollowingListResponse> followingList = userService.getFollowingList(memberFollowingListRequest);

        BaseResponse response = BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("OK")
                .data(followingList)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}