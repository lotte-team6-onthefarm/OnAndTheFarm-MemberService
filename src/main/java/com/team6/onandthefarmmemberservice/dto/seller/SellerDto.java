package com.team6.onandthefarmmemberservice.dto.seller;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SellerDto {
    private String email;
    private String password;
    private String zipcode;
    private String address;
    private String addressDetail;
    private String phone;
    private String name;
    private String shopName;
    private String businessNumber;
    private MultipartFile profile;
}
