package com.team6.onandthefarmmemberservice.service.user;

import com.team6.onandthefarmmemberservice.dto.following.MemberFollowingDto;
import com.team6.onandthefarmmemberservice.dto.user.UserInfoDto;
import com.team6.onandthefarmmemberservice.dto.user.UserLoginDto;
import com.team6.onandthefarmmemberservice.dto.user.UserQnaDto;
import com.team6.onandthefarmmemberservice.security.jwt.Token;
import com.team6.onandthefarmmemberservice.vo.following.*;
import com.team6.onandthefarmmemberservice.vo.user.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface UserService {


    UserTokenResponse login(UserLoginDto userLoginDto);

    Boolean logout(Long userId);

    Long registerUserInfo(UserInfoDto userInfoDto);

    Token reIssueToken(String refreshToken, HttpServletRequest request, HttpServletResponse response);

    Long updateUserInfo(UserInfoDto userInfoDto);

    UserInfoResponse findUserInfo(Long userId);

//    Boolean createProductQnA(UserQnaDto userQnaDto);
//
//    List<ProductQnAResponse> findUserQna(Long userId);
//
//    Boolean updateUserQna(UserQnaUpdateDto userQnaUpdateDto);
//
//    Boolean deleteUserQna(Long productQnaId);

    Long addFollowList(MemberFollowingDto memberFollowingDto);

    Long cancelFollowList(MemberFollowingDto memberFollowingDto);

    MemberFollowCountResponse getFollowingCount(MemberFollowCountRequest memberFollowCountRequest);

    List<MemberFollowerListResponse> getFollowerList(MemberFollowerListRequest memberFollowerListRequest);

    List<MemberFollowingListResponse> getFollowingList(MemberFollowingListRequest memberFollowingListRequest);
}
