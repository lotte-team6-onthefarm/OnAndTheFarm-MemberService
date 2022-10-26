package com.team6.onandthefarmmemberservice.entity.following;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SequenceGenerator(
		name="FOLLOWING_SEQ_GENERATOR",
		sequenceName = "FOLLOWING_SEQ",
		initialValue = 100000, allocationSize = 1
)
public class Following {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,
			generator = "FOLLOWING_SEQ_GENERATOR")
	private Long followingId;

	private Long followingMemberId;

	private String followingMemberRole;

	private Long followerMemberId;

	private String followerMemberRole;
}
