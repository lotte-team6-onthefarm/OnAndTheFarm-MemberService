package com.team6.onandthefarmmemberservice.controller;

import com.team6.onandthefarmmemberservice.dto.admin.AdminReIssueDto;
import com.team6.onandthefarmmemberservice.service.admin.AdminService;
import com.team6.onandthefarmmemberservice.utils.BaseResponse;
import com.team6.onandthefarmmemberservice.vo.admin.AdminLoginResponse;
import com.team6.onandthefarmmemberservice.vo.admin.AdminReIssueRequest;
import com.team6.onandthefarmmemberservice.vo.user.UserTokenResponse;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
@Slf4j
@RequestMapping("/api/admin/members")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService){
        this.adminService = adminService;
    }

    @PostMapping("/refresh")
    @ApiOperation(value = "refresh 토큰으로 access 토큰 재발급")
    public ResponseEntity<BaseResponse<UserTokenResponse>> refresh(@RequestBody AdminReIssueRequest adminReIssueRequest){

        AdminReIssueDto adminReIssueDto = new AdminReIssueDto();
        adminReIssueDto.setAccessToken(adminReIssueRequest.getAccessToken());
        adminReIssueDto.setRefreshToken(adminReIssueRequest.getRefreshToken());

        AdminLoginResponse adminLoginResponse = adminService.reIssueToken(adminReIssueDto);

        if(adminLoginResponse == null){
            BaseResponse response = BaseResponse.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("실패")
                    .build();

            return new ResponseEntity(response,HttpStatus.BAD_REQUEST);
        }

        BaseResponse response = BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("성공")
                .data(adminLoginResponse)
                .build();
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping("/logout")
    @ApiOperation(value = "셀러 로그아웃")
    public ResponseEntity<BaseResponse> logout(@ApiIgnore Principal principal, HttpServletRequest request){

        if(principal == null){
            BaseResponse baseResponse = BaseResponse.builder()
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .message("no authorization")
                    .build();
            return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
        }

        Boolean logoutStatus = adminService.logout(request);

        if(!logoutStatus){
            BaseResponse response = BaseResponse.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("실패")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        BaseResponse response = BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("성공")
                .build();

        return new ResponseEntity(response,HttpStatus.OK);
    }
}