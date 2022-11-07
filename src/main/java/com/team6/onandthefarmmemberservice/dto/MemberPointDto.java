package com.team6.onandthefarmmemberservice.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MemberPointDto {
    private Long memberId;

    private String orderSerial;
}
