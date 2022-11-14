package com.team6.onandthefarmmemberservice.feignclient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team6.onandthefarmmemberservice.dto.MemberPointDto;
import com.team6.onandthefarmmemberservice.entity.following.Following;
import com.team6.onandthefarmmemberservice.entity.seller.Seller;
import com.team6.onandthefarmmemberservice.entity.user.ReservedPoint;
import com.team6.onandthefarmmemberservice.entity.user.User;
import com.team6.onandthefarmmemberservice.feignclient.SnsServiceClient;
import com.team6.onandthefarmmemberservice.feignclient.vo.*;
import com.team6.onandthefarmmemberservice.kafka.PointOrderChannelAdapter;
import com.team6.onandthefarmmemberservice.repository.FollowingRepository;
import com.team6.onandthefarmmemberservice.repository.ReservedPointRepository;
import com.team6.onandthefarmmemberservice.repository.SellerRepository;
import com.team6.onandthefarmmemberservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberServiceClientServiceImp implements MemberServiceClientService{

    private final UserRepository userRepository;

    private final SellerRepository sellerRepository;

    private final FollowingRepository followingRepository;

    private final ReservedPointRepository reservedPointRepository;

    private final PointOrderChannelAdapter pointOrderChannelAdapter;

    private final SnsServiceClient snsServiceClient;

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
        SellerVo sellerVo = modelMapper.map(seller.get(),SellerVo.class);

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

    /**
     * 포인트 예약 테이블에 주문 정보를 db에 저장하는 메서드(try)
     * @param feedNumber 피드 serial number
     * @return
     */
    @Override
    public ReservedPoint reservedPoint(String feedNumber, String orderSerial) {
        String memberId = "";
        // feedId를 이용해서 memberId 가져오기
        memberId = String.valueOf(snsServiceClient.findByFeedNumber(Long.valueOf(feedNumber)).getMemberId());
        ReservedPoint reservedPoint = ReservedPoint.builder()
                .memberId(memberId)
                .orderSerial(orderSerial)
                .createdDate(LocalDateTime.now())
                .expireTime(LocalDateTime.now().plus(10l, ChronoUnit.SECONDS))
                .idempoStatus(false)
                .build();
        return reservedPointRepository.save(reservedPoint);
    }

    @Override
    public Boolean confirmOrder(Long id) {
        ReservedPoint reservedPoint = reservedPointRepository.findById(id).get();
        reservedPoint.validate(); // 예약 정보의 유효성 검증


        // ReservedPoint 상태 변경
        reservedPoint.setStatus("CONFIRMED");
        // 이후 confirm 메시지를 생성 및 전송(memberDto+orderserial)
        MemberPointDto memberPointDto = MemberPointDto.builder()
                .orderSerial(reservedPoint.getOrderSerial())
                .memberId(Long.valueOf(reservedPoint.getMemberId()))
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String message = ""; // confirm 메시지
        try{
            message = objectMapper.writeValueAsString(memberPointDto); // 포인트 메시지 생성
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        pointOrderChannelAdapter.producer(message);
        return Boolean.TRUE;
    }

    @Override
    public void cancelOrder(Long id) {
        ReservedPoint reservedPoint = reservedPointRepository.findById(id).get();
        reservedPoint.setStatus("CANCEL");
        log.info("Cancel Point :" + id);
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
