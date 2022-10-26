package com.team6.onandthefarmmemberservice.feignclient.service;

import com.team6.onandthefarmmemberservice.entity.user.User;
import com.team6.onandthefarmmemberservice.feignclient.vo.UserClientResponse;
import com.team6.onandthefarmmemberservice.feignclient.vo.UserVo;
import com.team6.onandthefarmmemberservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceClientServiceImp implements MemberServiceClientService{

    private final UserRepository userRepository;

    public UserVo findByUserId(Long userId){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        User user = userRepository.findById(userId).get();
        UserVo response = modelMapper.map(user,UserVo.class);

        return response;
    }
}
