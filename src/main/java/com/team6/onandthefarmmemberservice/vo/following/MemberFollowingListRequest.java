package com.team6.onandthefarmmemberservice.vo.following;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberFollowingListRequest {
	private Long memberId;
	private String memberRole;
	private Integer pageNumber;
	private Long loginMemberId;
	private String loginMemberRole;
}
