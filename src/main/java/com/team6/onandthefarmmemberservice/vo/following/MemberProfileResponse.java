package com.team6.onandthefarmmemberservice.vo.following;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileResponse {
    private String memberRole;
    private String memberName;
    private String memberProfileImage;
    private Boolean isModifiable;
    private Integer followingCount;
    private Integer followerCount;
    private Boolean followStatus;
}
