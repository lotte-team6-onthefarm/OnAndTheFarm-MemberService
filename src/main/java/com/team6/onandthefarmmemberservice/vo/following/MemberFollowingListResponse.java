package com.team6.onandthefarmmemberservice.vo.following;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberFollowingListResponse {
	private Long memberId;
	private String memberName;
	private String memberImg;
}