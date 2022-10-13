package com.team6.onandthefarmmemberservice.vo.user;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserQnaRequest {
    private Long productId;

    private String productQnaContent;

}
