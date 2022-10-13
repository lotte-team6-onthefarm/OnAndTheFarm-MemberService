package com.team6.onandthefarmmemberservice.dto.user;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserQnaUpdateDto {
    private Long productQnaId;

    private String productQnaContent;

    private Long userId;
}
