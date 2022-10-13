package com.team6.onandthefarmmemberservice.vo.following;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberFollowCountRequest {
	private Long memberId;
	private String memberRole;
}
