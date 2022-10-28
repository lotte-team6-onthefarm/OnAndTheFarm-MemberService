package com.team6.onandthefarmmemberservice.feignclient.service;

import com.team6.onandthefarmmemberservice.entity.seller.Seller;
import com.team6.onandthefarmmemberservice.entity.user.User;
import com.team6.onandthefarmmemberservice.feignclient.vo.SellerVo;
import com.team6.onandthefarmmemberservice.feignclient.vo.UserClientResponse;
import com.team6.onandthefarmmemberservice.feignclient.vo.UserClientUserShortInfoResponse;
import com.team6.onandthefarmmemberservice.feignclient.vo.UserVo;
import com.team6.onandthefarmmemberservice.repository.SellerRepository;
import com.team6.onandthefarmmemberservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceClientServiceImp implements MemberServiceClientService{

    private final UserRepository userRepository;

    private final SellerRepository sellerRepository;

    public UserVo findByUserId(Long userId){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        User user = userRepository.findById(userId).get();
        UserVo response = modelMapper.map(user,UserVo.class);

        return response;
    }

    public UserClientUserShortInfoResponse getShortInfoResponse(Long userId){
        User user = userRepository.findById(userId).get();
        UserClientUserShortInfoResponse userClientUserShortInfoResponse = UserClientUserShortInfoResponse.builder()
                .userName(user.getUserName())
                .userEmail(user.getUserEmail())
                .userProfileImg(user.getUserProfileImg())
                .build();
        return userClientUserShortInfoResponse;
    }

    public SellerVo findBySellerId(Long sellerId){
        Optional<Seller> seller = sellerRepository.findById(sellerId);

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SellerVo sellerVo = modelMapper.map(seller,SellerVo.class);

        return sellerVo;
    }
}
