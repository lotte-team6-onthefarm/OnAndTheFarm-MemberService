package com.team6.onandthefarmmemberservice.vo.following;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberProfileCountResponse {
    private Integer photoCount;
    private Integer scrapCount;
    private Integer wishCount;
}
