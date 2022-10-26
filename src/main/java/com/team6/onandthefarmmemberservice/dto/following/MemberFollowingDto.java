package com.team6.onandthefarmmemberservice.dto.following;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberFollowingDto {

	private Long followingMemberId;

	private String followingMemberRole;

	private Long followerMemberId;

	private String followerMemberRole;
}
