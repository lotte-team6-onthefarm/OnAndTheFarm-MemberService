package com.team6.onandthefarmmemberservice.vo.following;

import lombok.Data;

@Data
public class MemberProfileRequest {
    private Long memberId;
    private String memberRole;
}
