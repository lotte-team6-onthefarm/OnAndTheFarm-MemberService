package com.team6.onandthefarmmemberservice.controller;

import com.team6.onandthefarmmemberservice.dto.seller.EmailDto;
import com.team6.onandthefarmmemberservice.dto.seller.SellerDto;
import com.team6.onandthefarmmemberservice.security.jwt.Token;
import com.team6.onandthefarmmemberservice.service.seller.MailService;
import com.team6.onandthefarmmemberservice.service.seller.SellerService;
import com.team6.onandthefarmmemberservice.utils.BaseResponse;
import com.team6.onandthefarmmemberservice.utils.DateUtils;
import com.team6.onandthefarmmemberservice.vo.seller.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/seller")
@Api(value = "셀러",description = "QNA status\n" +
        "     * waiting(qna0) : 답변 대기\n" +
        "     * completed(qna1) : 답변 완료\n" +
        "     * deleted(qna2) : qna 삭제")
public class SellerController {

    private MailService mailService;

    private SellerService sellerService;

    private DateUtils dateUtils;

    private Environment env;


    @Autowired
    public SellerController(SellerService sellerService, MailService mailService, DateUtils dateUtils, Environment env) {
        this.sellerService = sellerService;
        this.mailService=mailService;
        this.dateUtils=dateUtils;
        this.env=env;
    }

    @GetMapping("/mypage/info")
    @ApiOperation(value = "셀러 회원 정보 조회")
    public ResponseEntity<SellerInfoResponse> findBySellerId(@ApiIgnore Principal principal){

        if(principal == null){
            BaseResponse baseResponse = BaseResponse.builder()
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .message("no authorization")
                    .build();
            return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
        }

        String[] principalInfo = principal.getName().split(" ");
        Long sellerId = Long.parseLong(principalInfo[0]);

        SellerInfoResponse response = sellerService.findByUserId(sellerId);
        return new ResponseEntity(response,HttpStatus.OK);
    }

    @PutMapping("/mypage/info")
    @ApiOperation(value = "셀러 회원 정보 수정")
    public ResponseEntity<BaseResponse> updateSeller(
            @ApiIgnore Principal principal,
            @RequestPart(value = "images", required = false) List<MultipartFile> profile,
            @RequestPart(value = "data", required = false) SellerUpdateRequest sellerUpdateRequest)
            throws Exception{

        if(principal == null){
            BaseResponse baseResponse = BaseResponse.builder()
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .message("no authorization")
                    .build();
            return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
        }

        String[] principalInfo = principal.getName().split(" ");
        Long sellerId = Long.parseLong(principalInfo[0]);

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SellerDto sellerDto = modelMapper.map(sellerUpdateRequest,SellerDto.class);
        sellerDto.setProfile(profile.get(0));
        sellerService.updateByUserId(sellerId, sellerDto);

        BaseResponse response = BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("성공")
                .build();

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping("/login")
    @ApiOperation(value = "셀러 로그인")
    public ResponseEntity<BaseResponse<SellerLoginResponse>> login(@RequestBody SellerRequest sellerRequest){

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SellerDto sellerDto = modelMapper.map(sellerRequest, SellerDto.class);

        Token token = sellerService.login(sellerDto);

        SellerLoginResponse sellerLoginResponse = new SellerLoginResponse();
        sellerLoginResponse.setToken(token);

        BaseResponse response = BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("성공")
                .data(sellerLoginResponse)
                .build();

        if(token == null){
            response = BaseResponse.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("실패")
                    .data(sellerLoginResponse)
                    .build();

            return new ResponseEntity(response,HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(response,HttpStatus.OK);
    }

    @PostMapping("/signup")
    @ApiOperation(value = "셀러 회원가입")
    public ResponseEntity<BaseResponse> signup(@RequestBody SellerRequest sellerRequest){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SellerDto sellerDto = modelMapper.map(sellerRequest,SellerDto.class);
        BaseResponse responseOk = BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("OK")
                .build();
        BaseResponse responseBadrequest = BaseResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message("Bad request")
                .build();
        if(!sellerService.sellerSignup(sellerDto)){
            return new ResponseEntity(responseBadrequest,HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(responseOk,HttpStatus.OK);
    }

    @PostMapping("/search/id")
    @ApiOperation(value = "셀러 아이디 찾기")
    public ResponseEntity<BaseResponse> searchSellerId(@RequestBody Map<String,String> map){
        String sellerEmail = map.get("sellerEmail");
        String phone = map.get("phone");
        Boolean result = sellerService.searchSellerId(sellerEmail,phone);
        if(result){
            BaseResponse response = BaseResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("OK")
                    .build();
            return new ResponseEntity(response,HttpStatus.OK);
        }
        BaseResponse response = BaseResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message("정보 없음")
                .build();
        return new ResponseEntity(response,HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/search/passwd")
    @ApiOperation(value = "셀러 비밀번호 찾기")
    public ResponseEntity<BaseResponse> searchSellerPasswd(@RequestBody Map<String,String> map){
        String sellerEmail = map.get("sellerEmail");
        String phone = map.get("phone");
        Boolean result = sellerService.searchSellerId(sellerEmail,phone);
        if(result){
            BaseResponse response = BaseResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("OK")
                    .build();
            return new ResponseEntity(response,HttpStatus.OK);
        }
        BaseResponse response = BaseResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message("정보 없음")
                .build();
        return new ResponseEntity(response,HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/passwd")
    @ApiOperation(value = "셀러 비밀번호 변경")
    public ResponseEntity<BaseResponse> changePassword(@RequestBody SellerPasswordRequest sellerPasswordRequest){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SellerDto sellerDto = modelMapper.map(sellerPasswordRequest,SellerDto.class);
        sellerService.updatePassword(sellerDto);
        BaseResponse response = BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("OK")
                .build();
        return new ResponseEntity(response,HttpStatus.OK);
    }

    @PostMapping("/email") // 인증버튼 누름
    @ApiOperation(value = "이메일 인증")
    public ResponseEntity<BaseResponse> emailAuth(@RequestBody EmailRequest emailRequest){
        if(!sellerService.sellerIdCheck(emailRequest.getEmail())){ // email 중복확인
            BaseResponse response = BaseResponse.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("이메일 중복됨!")
                    .build();
            return new ResponseEntity(response,HttpStatus.BAD_REQUEST);
        }
        String authKey = mailService.sendAuthMail(emailRequest.getEmail());
        String date = dateUtils.transDate(env.getProperty("dateutils.format"));
        EmailDto email = EmailDto.builder()
                .email(emailRequest.getEmail())
                .authKey(authKey)
                .date(date)
                .build();
        mailService.save(email);
        BaseResponse response = BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("OK")
                .build();
        return new ResponseEntity(response,HttpStatus.OK);
    }

    @GetMapping("/emailConfirm") // 인증번호 확인
    @ApiOperation(value = "이메일 인증확인")
    public ResponseEntity<BaseResponse> signUpConfirm(@RequestParam Map<String, String> map){
        boolean result = mailService.checkAuthKey(map);
        if(result){
            BaseResponse response = BaseResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("이메일 인증 성공")
                    .build();
            return new ResponseEntity(response,HttpStatus.OK);
        }
        BaseResponse response = BaseResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message("이메일 인증 실패")
                .build();
        return new ResponseEntity(response,HttpStatus.BAD_REQUEST);
    }

//    @GetMapping("/QnA")
//    @ApiOperation(value = "셀러의 전체 질의 조회")
//    public ResponseEntity<BaseResponse<SellerProductQnaResponseResult>> findSellerQnA (
//            @ApiIgnore Principal principal, @RequestParam String pageNumber){
//
//        if(principal == null){
//            BaseResponse baseResponse = BaseResponse.builder()
//                    .httpStatus(HttpStatus.FORBIDDEN)
//                    .message("no authorization")
//                    .build();
//            return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
//        }
//
//        String[] principalInfo = principal.getName().split(" ");
//        Long sellerId = Long.parseLong(principalInfo[0]);
//
//        SellerProductQnaResponseResult productQnas
//                = sellerService.findSellerQnA(sellerId,Integer.valueOf(pageNumber));
//        BaseResponse response = BaseResponse.builder()
//                .httpStatus(HttpStatus.OK)
//                .message("OK")
//                .data(productQnas)
//                .build();
//        return new ResponseEntity(response,HttpStatus.OK);
//    }
//
//    @PostMapping("/QnA")
//    @ApiOperation(value = "셀러의 질의 처리")
//    public ResponseEntity<BaseResponse> createSellerQnaAnswer (@RequestBody SellerProductQnaAnswerRequest request){
//        ModelMapper modelMapper = new ModelMapper();
//        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//        SellerQnaDto sellerQnaDto = modelMapper.map(request, SellerQnaDto.class);
//        Boolean result = sellerService.createQnaAnswer(sellerQnaDto);
//        BaseResponse response = BaseResponse.builder()
//                .httpStatus(HttpStatus.OK)
//                .message("OK")
//                .data(result)
//                .build();
//        return new ResponseEntity(response,HttpStatus.OK);
//    }
//
//    @GetMapping("/mypage")
//    @ApiOperation(value = "셀러의 메인페이지 조회")
//    public ResponseEntity<BaseResponse<SellerMypageResponse>> findSellerMypage(
//            @ApiIgnore Principal principal, @RequestParam Map<String,String> map){
//
//        if(principal == null){
//            BaseResponse baseResponse = BaseResponse.builder()
//                    .httpStatus(HttpStatus.FORBIDDEN)
//                    .message("no authorization")
//                    .build();
//            return new ResponseEntity(baseResponse, HttpStatus.BAD_REQUEST);
//        }
//
//        String[] principalInfo = principal.getName().split(" ");
//        Long sellerId = Long.parseLong(principalInfo[0]);
//
//        String startDate = map.get("startDate").substring(0,10)+" 00:00:00";
//        String endDate = map.get("endDate").substring(0,10)+" 23:59:59";
//
//        ModelMapper modelMapper = new ModelMapper();
//        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//        SellerMypageRequest sellerMypageRequest = SellerMypageRequest.builder()
//                .startDate(startDate)
//                .endDate(endDate)
//                .build();
//        SellerMypageDto sellerMypageDto = modelMapper.map(sellerMypageRequest, SellerMypageDto.class);
//        sellerMypageDto.setSellerId(sellerId);
//
//        SellerMypageResponse mypageResponse = sellerService.findSellerMypage(sellerMypageDto);
//
//        BaseResponse response = BaseResponse.builder()
//                .httpStatus(HttpStatus.OK)
//                .message("OK")
//                .data(mypageResponse)
//                .build();
//
//        return new ResponseEntity(response,HttpStatus.OK);
//    }

}
