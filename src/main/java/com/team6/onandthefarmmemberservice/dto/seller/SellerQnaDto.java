package com.team6.onandthefarmmemberservice.dto.seller;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SellerQnaDto {
    private String productQnaId;

    private String productQnaAnswerContent;
}
