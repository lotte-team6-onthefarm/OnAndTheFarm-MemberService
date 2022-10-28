package com.team6.onandthefarmmemberservice.feignclient.vo;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowingVo {

    private Long followingId;

    private Long followingMemberId;

    private String followingMemberRole;

    private Long followerMemberId;

    private String followerMemberRole;

}
