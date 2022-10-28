package com.team6.onandthefarmmemberservice.feignclient.service;

import com.team6.onandthefarmmemberservice.entity.following.Following;
import com.team6.onandthefarmmemberservice.entity.seller.Seller;
import com.team6.onandthefarmmemberservice.entity.user.User;
import com.team6.onandthefarmmemberservice.feignclient.vo.*;
import com.team6.onandthefarmmemberservice.repository.FollowingRepository;
import com.team6.onandthefarmmemberservice.repository.SellerRepository;
import com.team6.onandthefarmmemberservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceClientServiceImp implements MemberServiceClientService{

    private final UserRepository userRepository;

    private final SellerRepository sellerRepository;

    private final FollowingRepository followingRepository;

    public UserVo findByUserId(Long userId){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        User user = userRepository.findById(userId).get();
        UserVo response = modelMapper.map(user,UserVo.class);

        return response;
    }

    public UserClientUserShortInfoResponse getShortInfoResponse(Long userId){
        User user = userRepository.findById(userId).get();
        UserClientUserShortInfoResponse userClientUserShortInfoResponse = UserClientUserShortInfoResponse.builder()
                .userName(user.getUserName())
                .userEmail(user.getUserEmail())
                .userProfileImg(user.getUserProfileImg())
                .build();
        return userClientUserShortInfoResponse;
    }

    public SellerVo findBySellerId(Long sellerId){
        Optional<Seller> seller = sellerRepository.findById(sellerId);

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SellerVo sellerVo = modelMapper.map(seller,SellerVo.class);

        return sellerVo;
    }

    @Override
    public FollowingVo findByFollowingMemberIdAndFollowerMemberId(Long followingMemberId, Long followerMemberId) {

        FollowingVo followingVo = new FollowingVo();

        Optional<Following> following = followingRepository.findByFollowingMemberIdAndFollowerMemberId(followingMemberId, followerMemberId);
        if(following.isPresent()){
            followingVo.setFollowingId(following.get().getFollowingId());
            followingVo.setFollowingMemberId(following.get().getFollowingMemberId());
            followingVo.setFollowingMemberRole(following.get().getFollowingMemberRole());
            followingVo.setFollowerMemberId(following.get().getFollowerMemberId());
            followingVo.setFollowerMemberRole(following.get().getFollowerMemberRole());
        }

        return followingVo;
    }

    @Override
    public List<FollowingVo> findByFollowingMemberId(Long memberId) {

        List<FollowingVo> followingVoList = new ArrayList<>();

        List<Following> followingList = followingRepository.findFollowerIdByFollowingId(memberId);
        for(Following following : followingList){
            FollowingVo followingVo = FollowingVo.builder()
                    .followingId(following.getFollowingId())
                    .followingMemberId(following.getFollowingMemberId())
                    .followingMemberRole(following.getFollowingMemberRole())
                    .followerMemberId(following.getFollowerMemberId())
                    .followerMemberRole(following.getFollowerMemberRole())
                    .build();

            followingVoList.add(followingVo);
        }

        return followingVoList;
    }
}
