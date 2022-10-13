package com.team6.onandthefarmmemberservice.vo.following;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberFollowRequest {
	private Long followerMemberId;
	private String followerMemberRole;
}
