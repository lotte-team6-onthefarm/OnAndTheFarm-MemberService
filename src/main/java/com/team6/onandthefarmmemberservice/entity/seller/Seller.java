package com.team6.onandthefarmmemberservice.entity.seller;

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
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "MEMBER_SEQ_GENERATOR")
    private Long sellerId;
    private String sellerEmail;
    private String sellerPassword;
    private String sellerZipcode;
    private String sellerAddress;
    private String sellerAddressDetail;
    private String sellerPhone;
    private String sellerName;
    private String sellerShopName;
    private String sellerBusinessNumber;
    private String sellerRegisterDate;
    private Boolean sellerIsActivated;
    private String role;
    private Integer sellerFollowingCount;
    private Integer sellerFollowerCount;
    private String sellerProfileImg;
}
