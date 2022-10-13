package com.team6.onandthefarmmemberservice.entity.seller;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Builder
@Slf4j
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
    private Boolean sellerIsActived;
    private String role;
    private Integer sellerFollowingCount;
    private Integer sellerFollowerCount;
    private String sellerProfileImg;
}
