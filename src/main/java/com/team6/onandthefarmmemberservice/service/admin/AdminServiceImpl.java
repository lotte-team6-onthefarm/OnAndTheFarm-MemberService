package com.team6.onandthefarmmemberservice.service.admin;

import com.team6.onandthefarmmemberservice.dto.admin.AdminReIssueDto;
import com.team6.onandthefarmmemberservice.entity.admin.Admin;
import com.team6.onandthefarmmemberservice.repository.AdminRepository;
import com.team6.onandthefarmmemberservice.security.jwt.JwtTokenUtil;
import com.team6.onandthefarmmemberservice.security.jwt.Token;
import com.team6.onandthefarmmemberservice.utils.RedisUtil;
import com.team6.onandthefarmmemberservice.vo.admin.AdminLoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.Duration;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class AdminServiceImpl implements AdminService{

    private final AdminRepository adminRepository;
    private final RedisUtil redisUtil;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository,
                            RedisUtil redisUtil,
                            JwtTokenUtil jwtTokenUtil){
        this.adminRepository = adminRepository;
        this.redisUtil = redisUtil;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public AdminLoginResponse reIssueToken(AdminReIssueDto adminReIssueDto) {
        // access & refresh token 가져오기
        String refreshToken = adminReIssueDto.getRefreshToken();

        Long adminId = Long.parseLong(redisUtil.getValues(refreshToken));
        Optional<Admin> savedAdmin = adminRepository.findById(adminId);
        if(savedAdmin.isPresent()){
            if(!jwtTokenUtil.checkExpiredToken(refreshToken)) {
                // Token 재생성
                Token newToken = jwtTokenUtil.generateToken(adminId, savedAdmin.get().getRole());

                // 기존 refresh Token 삭제
                redisUtil.deleteValues(refreshToken);

                // 새 refresh token redis 저장
                Long newRefreshTokenExpiration = jwtTokenUtil.getTokenExpirationAsLong(newToken.getRefreshToken());
                Duration expireDuration = Duration.ofMillis(newRefreshTokenExpiration);
                redisUtil.setValueDuration(newToken.getRefreshToken(), Long.toString(adminId), expireDuration);

                AdminLoginResponse adminTokenResponse = AdminLoginResponse.builder()
                        .token(newToken)
                        .role(savedAdmin.get().getRole())
                        .build();

                return adminTokenResponse;
            }

        }
        return null;
    }

    @Override
    public Boolean logout(HttpServletRequest request) {
        try {
            String accessToken = request.getHeader("Authorization");

            // access Token 블랙리스트 추가
            Integer tokenExpiration = jwtTokenUtil.getTokenExpirationAsLong(accessToken).intValue();
            Duration expireDuration = Duration.ofMillis(tokenExpiration);
            redisUtil.setValueDuration(accessToken, "BlackList", expireDuration);

        } catch (Exception e) {
            log.error(String.valueOf(e));
            return false;
        }
        return true;
    }
}