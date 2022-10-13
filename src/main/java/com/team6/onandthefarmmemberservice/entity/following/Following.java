package com.team6.onandthefarmmemberservice.entity.following;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Builder
@Slf4j
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Following {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long followingId;

	private Long followingMemberId;

	private String followingMemberRole;

	private Long followerMemberId;

	private String followerMemberRole;
}
