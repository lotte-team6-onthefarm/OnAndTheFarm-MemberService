package com.team6.onandthefarmmemberservice.vo.following;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberFollowResult {

    private List<MemberFollowListResponse> memberFollowListResponseList;

    private Integer currentPageNum;

    private Integer totalPageNum;

    private Integer totalElementNum;

}
