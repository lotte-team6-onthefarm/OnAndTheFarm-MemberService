package com.team6.onandthefarmmemberservice.service.user;

import com.team6.onandthefarmmemberservice.dto.following.MemberFollowingDto;
import com.team6.onandthefarmmemberservice.dto.following.MemberProfileDto;
import com.team6.onandthefarmmemberservice.dto.user.UserInfoDto;
import com.team6.onandthefarmmemberservice.dto.user.UserLoginDto;
import com.team6.onandthefarmmemberservice.security.jwt.Token;
import com.team6.onandthefarmmemberservice.vo.following.*;
import com.team6.onandthefarmmemberservice.vo.user.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface UserService {

    UserTokenResponse login(UserLoginDto userLoginDto);

    Boolean logout(Long userId);

    Boolean loginPhoneConfirm(String phone);

    Token reIssueToken(String refreshToken, HttpServletRequest request, HttpServletResponse response);

    Long updateUserInfo(UserInfoDto userInfoDto) throws IOException;

    UserInfoResponse findUserInfo(Long userId);

    Long addFollowList(MemberFollowingDto memberFollowingDto);

    Long cancelFollowList(MemberFollowingDto memberFollowingDto);

    MemberFollowResult getFollowerList(MemberFollowerListRequest memberFollowerListRequest);

    MemberFollowResult getFollowingList(MemberFollowingListRequest memberFollowingListRequest);

    MemberProfileResponse getMemberProfile(MemberProfileDto memberProfileDto);

}
