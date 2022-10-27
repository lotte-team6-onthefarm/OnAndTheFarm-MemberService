package com.team6.onandthefarmmemberservice.feignclient.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserClientUserShortInfoResponse {
	private String userProfileImg;
	private String userEmail;
	private String userName;
}
