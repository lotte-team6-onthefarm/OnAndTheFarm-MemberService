package com.team6.onandthefarmmemberservice.service.seller;

import com.team6.onandthefarmmemberservice.dto.seller.EmailDto;
import com.team6.onandthefarmmemberservice.entity.seller.EmailConfirmation;
import com.team6.onandthefarmmemberservice.repository.EmailRepository;
import com.team6.onandthefarmmemberservice.utils.DateUtils;
import com.team6.onandthefarmmemberservice.utils.MailUtils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@Transactional
@Slf4j
public class MailServiceImpl implements MailService{
    private JavaMailSenderImpl mailSender;

    private EmailRepository emailRepository;

    private DateUtils dateUtils;

    private Environment env;


    private int size;

    @Autowired
    public MailServiceImpl(JavaMailSenderImpl mailSender, EmailRepository emailRepository, DateUtils dateUtils, Environment env) {
        this.mailSender = mailSender;
        this.emailRepository=emailRepository;
        this.dateUtils=dateUtils;
        this.env=env;
    }

    /**
     * 이메일 인증 row 추가
     * @param emailDto
     */
    public void save(EmailDto emailDto){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        EmailConfirmation email = EmailConfirmation.builder()
                .emailId(emailDto.getEmail())
                .authKey(emailDto.getAuthKey())
                .confirmDate(emailDto.getDate())
                .build();
        emailRepository.save(email);
    }


    /**
     * 시간 5분 체크와 최근 1건에 대한 인증
     * @param map(authKey / email)
     * @return true: 인증완료 false: 인증실패
     */
    public boolean checkAuthKey(Map<String, String> map) {

        String AuthKey = map.get("authKey");
        String email = map.get("email");
        EmailConfirmation emailConfirmation = emailRepository.findByEmailIdAndAuthKey(email,AuthKey);
        if(emailConfirmation == null){
            return false;
        }

        EmailConfirmation recentEmailConfirmation = emailRepository.findTopByEmailIdOrderByConfirmDateDesc(email);
        if(!emailConfirmation.getAuthKey().equals(recentEmailConfirmation.getAuthKey())){
            return false;
        }

        String nowStr = dateUtils.transDate(env.getProperty("dateutils.format"));
        String dateStr = recentEmailConfirmation.getConfirmDate();

        String[] date = emailConfirmation.getConfirmDate().substring(11).split(":");
        String[] nowDate = dateStr.substring(11).split(":");
        if(!nowStr.substring(0,10).equals(dateStr.substring(0,10))){ // 오늘인지 확인
            return false;
        }


        int HD = Integer.valueOf(date[0]);
        int mD = Integer.valueOf(date[1]);
        int sD = Integer.valueOf(date[2]);
        int totalD = (HD*60*60)+(mD*60)+sD;
        int H = Integer.valueOf(nowDate[0]);
        int m = Integer.valueOf(nowDate[1]);
        int s = Integer.valueOf(nowDate[2]);
        int total = (H*60*60)+(m*60)+s;

        if(total-totalD>300){ // 5분 체크
            return false;
        }

        /*
            인증완료 시 email_confirmation에 있는 row들 다 삭제됨
         */
        List<EmailConfirmation> emailConfirmationList = emailRepository.findAllByEmailId(email);
        for(EmailConfirmation emails : emailConfirmationList){
            emailRepository.delete(emails);
        }
        return true;
    }

    private String getKey(int size) {
        this.size = size;
        return getAuthCode();
    }

    //인증코드 난수 발생
    private String getAuthCode() {
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        int num = 0;

        while(buffer.length() < size) {
            num = random.nextInt(10);
            buffer.append(num);
        }

        return buffer.toString();
    }

    //인증메일 보내기
    public String sendAuthMail(String email) {
        //6자리 난수 인증번호 생성
        String authKey = getKey(6);

        //인증메일 보내기
        try {
            MailUtils sendMail = new MailUtils(mailSender);
            sendMail.setSubject("회원가입 이메일 인증");
            sendMail.setText(new StringBuffer().append("<h1>[이메일 인증]</h1>")
                    .append("<p>아래 링크를 클릭하시면 이메일 인증이 완료됩니다.</p>")
                    .append("&authKey=")
                    .append(authKey)
                    .toString());
            sendMail.setFrom("ksh9409255@gmail.com", "관리자"); // 관리자 email 작성해주고 관리자 email의 경우 2차비밀번호 인증을 해야함
            sendMail.setTo(email);
            sendMail.send();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return authKey;
    }
}
