package com.team6.onandthefarmmemberservice.dto.user;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class UserInfoDto {

    private Long userId;

    private String userZipcode;

    private String userAddress;

    private String userAddressDetail;

    private String userPhone;

    private String userBirthday;

    private Integer userSex;

    private String userName;

    private MultipartFile profile;
}
