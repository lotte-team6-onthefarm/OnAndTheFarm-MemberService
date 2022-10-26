package com.team6.onandthefarmmemberservice.entity.user;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Builder
@Slf4j
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SequenceGenerator(
        name="MEMBER_SEQ_GENERATOR",
        sequenceName = "MEMBER_SEQ",
        initialValue = 100000, allocationSize = 1
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "MEMBER_SEQ_GENERATOR")
    private Long userId;

    private String userEmail;

    private String userZipcode;

    private String userAddress;

    private String userAddressDetail;

    private String userPhone;

    private String userBirthday;

    private Integer userSex;

    private String userName;

    private String userRegisterDate;

    private Boolean userIsActivated;

    private String role;

    private String provider;

    private Long userKakaoNumber;

    private String userNaverNumber;

    private String userAppleNumber;

    private String userGoogleNumber;

    private Integer userFollowingCount;

    private Integer userFollowerCount;

    private String userProfileImg;
}
