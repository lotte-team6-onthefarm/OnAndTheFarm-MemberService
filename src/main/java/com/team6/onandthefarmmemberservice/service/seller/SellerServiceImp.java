package com.team6.onandthefarmmemberservice.service.seller;


import com.team6.onandthefarmmemberservice.dto.seller.SellerDto;
import com.team6.onandthefarmmemberservice.entity.admin.Admin;
import com.team6.onandthefarmmemberservice.entity.seller.Seller;
import com.team6.onandthefarmmemberservice.repository.AdminRepository;
import com.team6.onandthefarmmemberservice.repository.SellerRepository;
import com.team6.onandthefarmmemberservice.security.jwt.JwtTokenUtil;
import com.team6.onandthefarmmemberservice.security.jwt.Token;
import com.team6.onandthefarmmemberservice.utils.DateUtils;
import com.team6.onandthefarmmemberservice.utils.S3Upload;
import com.team6.onandthefarmmemberservice.vo.seller.SellerInfoResponse;
import com.team6.onandthefarmmemberservice.vo.seller.SellerLoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
@Transactional
public class SellerServiceImp implements SellerService{

    private final AdminRepository adminRepository;

    private final SellerRepository sellerRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private DateUtils dateUtils;

    private S3Upload s3Upload;

    private final JwtTokenUtil jwtTokenUtil;

    private Environment env;


    @Autowired
    public SellerServiceImp(AdminRepository adminRepository,
                            SellerRepository sellerRepository,
                            BCryptPasswordEncoder bCryptPasswordEncoder,
                            DateUtils dateUtils,
                            Environment env,
                            JwtTokenUtil jwtTokenUtil,
                            S3Upload s3Upload) {
        this.adminRepository = adminRepository;
        this.sellerRepository = sellerRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.dateUtils=dateUtils;
        this.env=env;
        this.jwtTokenUtil = jwtTokenUtil;
        this.s3Upload=s3Upload;
    }

    /**
     * 셀러의 로그인 메소드
     *
     * @param sellerDto
     * @return token
     */
    @Override
    public SellerLoginResponse login(SellerDto sellerDto) {

        SellerLoginResponse sellerLoginResponse = new SellerLoginResponse();

        if(sellerDto.getEmail().equals("admin")){
            Optional<Admin> admin = adminRepository.findAdminByAdminEmailAndAdminPassword(sellerDto.getEmail(), sellerDto.getPassword());

            if(admin.isPresent()) {
                Token token = jwtTokenUtil.generateToken(admin.get().getAdminId(), admin.get().getRole());
                sellerLoginResponse.setToken(token);
                sellerLoginResponse.setRole("admin");

                return sellerLoginResponse;
            }
        }

        Optional<Seller> seller = sellerRepository.findBySellerEmail(sellerDto.getEmail());
        if(seller.isPresent()){
            if(bCryptPasswordEncoder.matches(sellerDto.getPassword(), seller.get().getSellerPassword())) {
                Token token = jwtTokenUtil.generateToken(seller.get().getSellerId(), seller.get().getRole());
                sellerLoginResponse.setToken(token);
                sellerLoginResponse.setRole("seller");
            }
        }

        return sellerLoginResponse;
    }

    @Override
    public boolean updateByUserId(Long userId, SellerDto sellerDto) throws IOException {
        Optional<Seller> sellerEntity = sellerRepository.findById(userId);

        sellerEntity.get().setSellerZipcode(sellerDto.getZipcode());
        sellerEntity.get().setSellerAddress(sellerDto.getAddress());
        sellerEntity.get().setSellerAddressDetail(sellerDto.getAddressDetail());
        sellerEntity.get().setSellerShopName(sellerDto.getShopName());
        sellerEntity.get().setSellerPhone(sellerDto.getPhone());

        String url = s3Upload.profileSellerUpload(sellerDto.getProfile());
        sellerEntity.get().setSellerProfileImg(url);

        return true;
    }

    /**
     * 셀러 유저의 정보를 조회하는 메서드
     * @param userId
     * @return
     */
    @Override
    public SellerInfoResponse findByUserId(Long userId){
        Optional<Seller> sellerEntity = sellerRepository.findById(userId);
        Seller seller = sellerEntity.get();

        SellerInfoResponse response = SellerInfoResponse.builder()
                .email(seller.getSellerEmail())
                .zipcode(seller.getSellerZipcode())
                .address(seller.getSellerAddress())
                .addressDetail(seller.getSellerAddressDetail())
                .phone(seller.getSellerPhone())
                .name(seller.getSellerName())
                .businessNumber(seller.getSellerBusinessNumber())
                .registerDate(seller.getSellerRegisterDate())
                .sellerProfileImg(seller.getSellerProfileImg())
                .build();
        return response;
    }

    /**
     * 회원의 비밀번호값을 변경해주는 메서드
     * @param sellerDto
     */
    @Override
    public Boolean updatePassword(SellerDto sellerDto){
        Optional<Seller> seller = sellerRepository.findBySellerEmail(sellerDto.getEmail());

        String encodePassword = bCryptPasswordEncoder.encode(sellerDto.getPassword());
        seller.get().setSellerPassword(encodePassword);
        if(seller.get().getSellerPassword().equals(encodePassword)){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public String searchSellerId(String name, String phone){
        Optional<Seller> seller = sellerRepository.findBySellerNameAndAndSellerPhone(name,phone);
        if(!seller.isPresent()){
            return "";
        }
        return seller.get().getSellerEmail();
    }

    public Boolean searchSellerpasswd(String sellerEmail, String name){
        Optional<Seller> seller = sellerRepository.findBySellerEmailAndSellerName(sellerEmail,name);

        if(!seller.isPresent()){
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     *
     * @param sellerDto
     * @return true: 회원가입 됨  false: 회원가입 실패
     */
    @Override
    public boolean sellerSignup(SellerDto sellerDto){
        String encodePassword = bCryptPasswordEncoder.encode(sellerDto.getPassword());

        Optional<Seller> savedSeller = sellerRepository.findBySellerEmail(sellerDto.getEmail());
        if(savedSeller.isPresent()){
            return false;
        }

        String date = dateUtils.transDate(env.getProperty("dateutils.format"));
        Seller seller = Seller.builder()
                .sellerEmail(sellerDto.getEmail())
                .sellerAddress(sellerDto.getAddress())
                .sellerAddressDetail(sellerDto.getAddressDetail())
                .sellerBusinessNumber(sellerDto.getBusinessNumber())
                .sellerName(sellerDto.getName())
                .sellerPassword(encodePassword)
                .sellerPhone(sellerDto.getPhone())
                .sellerZipcode(sellerDto.getZipcode())
                .sellerRegisterDate(date)
                .sellerShopName(sellerDto.getShopName())
                .sellerIsActivated(Boolean.TRUE)
                .role("ROLE_SELLER")
                .sellerFollowerCount(0)
                .sellerFollowingCount(0)
                .sellerProfileImg("https://lotte-06-s3-test.s3.ap-northeast-2.amazonaws.com/profile/seller/basic_profile.png")
                .build();

        seller.setSellerRegisterDate(date);
        sellerRepository.save(seller);
        return true;
    }

    /**
     *
     * @param sellerEmail
     * @return true: 중복안됨 / false: 중복됨
     */
    @Override
    public boolean sellerIdCheck(String sellerEmail){
        Optional<Seller> email = sellerRepository.findBySellerEmail(sellerEmail);
        if(!email.isPresent()){
            return true;
        }
        return false;
    }

}
