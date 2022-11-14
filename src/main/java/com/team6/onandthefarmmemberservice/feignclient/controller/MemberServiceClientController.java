package com.team6.onandthefarmmemberservice.feignclient.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team6.onandthefarmmemberservice.ParticipantLink;
import com.team6.onandthefarmmemberservice.entity.user.ReservedPoint;
import com.team6.onandthefarmmemberservice.feignclient.service.MemberServiceClientService;
import com.team6.onandthefarmmemberservice.feignclient.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberServiceClientController {

    private final MemberServiceClientService memberServiceClientService;

    /**
     * 유저ID를 이용해서 유저 정보를 가져오는 것
     * @param userId
     * @return
     */
    @GetMapping("/api/feign/user/members/member-service/{user-no}")
    public UserVo findByUserId(@PathVariable("user-no") Long userId){
        return memberServiceClientService.findByUserId(userId);
    }

    @GetMapping("/api/feign/user/members/member-service/short-info/{user-no}")
    UserClientUserShortInfoResponse findUserNameByUserId(@PathVariable("user-no") Long userId){
        return memberServiceClientService.getShortInfoResponse(userId);
    }

    @GetMapping("/api/feign/seller/members/member-service/{seller-no}")
    SellerVo findBySellerId(@PathVariable("seller-no")Long sellerId){
        return memberServiceClientService.findBySellerId(sellerId);
    }

    @GetMapping("/api/feign/user/members/member-service/following")
    FollowingVo findByFollowingMemberIdAndFollowerMemberId(@RequestParam Long followingMemberId, @RequestParam Long followerMemberId){
        return memberServiceClientService.findByFollowingMemberIdAndFollowerMemberId(followingMemberId, followerMemberId);
    }

    @GetMapping("/api/feign/user/members/member-service/following/list/{member-no}")
    List<FollowingVo> findByFollowingMemberId(@PathVariable("member-no")Long memberId){
        return memberServiceClientService.findByFollowingMemberId(memberId);
    }

    /**
     * order-service에서 product-service로 주문을 try하는 메서드
     * @param map : 주문 정보를 가진 객체 productIdList : List<OrderProduct>
     * @return participantLink객체를 리턴하며, confirm을 위한 url이 존재한다.
     */
    @PostMapping("/api/feign/user/members/member-service/member-try")
    public ResponseEntity<ParticipantLink> orderTry(@RequestBody Map<String, Object> map){
        String feedNumber = "";
        String orderSerial = "";

        ObjectMapper objectMapper = new ObjectMapper();
        try{
            // 상품들의 정보를 직렬화
            feedNumber = objectMapper.writeValueAsString(map.get("feedNumber"));
            feedNumber = feedNumber.substring(1,feedNumber.length()-1);
            orderSerial = objectMapper.writeValueAsString(map.get("orderSerial"));
            orderSerial = orderSerial.substring(1,orderSerial.length()-1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        // 포인트 예약 테이블에 예약 저장
        ReservedPoint reservedPoint = memberServiceClientService.reservedPoint(feedNumber,orderSerial);

        final ParticipantLink participantLink = buildParticipantLink(
                reservedPoint.getReservedPointId(),
                reservedPoint.getExpireTime());
        return new ResponseEntity<>(participantLink, HttpStatus.CREATED);
    }

    /**
     * 재고 차감을 확정해주는 메서드
     * @param id
     * @return
     */
    @PutMapping("/api/feign/user/members/member-service/member-try/{id}")
    public ResponseEntity<Void> confirmOrderAdjustment(@PathVariable Long id) {
        try {
            memberServiceClientService.confirmOrder(id);
        } catch(IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * confirm시 사용될 url 및 timeExpire를 생성해주는 메서드
     * @param id : 예약 테이블에 저장되는 row의 pk값을 의미
     * @param expire : timeout 시간의미
     * @return location은 confirm시 사용될 url이다.ParticipantLink
     */
    private ParticipantLink buildParticipantLink(final Long id, LocalDateTime expire) {
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
        return new ParticipantLink(location, expire);
    }


    /**
     * 주문 생성 시 오류가 날 경우 예약된 주문 정보를 cancel해주는 메서드
     * @param id
     * @return
     */
    @DeleteMapping("/api/feign/user/members/member-service/member-try/{id}")
    public ResponseEntity<Void> cancelOrderAdjustment(@PathVariable Long id) {
        memberServiceClientService.cancelOrder(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
