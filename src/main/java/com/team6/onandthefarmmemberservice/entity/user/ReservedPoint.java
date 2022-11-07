package com.team6.onandthefarmmemberservice.entity.user;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Builder
@Slf4j
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReservedPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reservedPointId;

    private String orderSerial;

    private String memberId;

    private LocalDateTime createdDate;

    private LocalDateTime expireTime;

    private String status; // cancel / confirm 상태

    private Boolean idempoStatus; // 멱등성을 위한 상태 / true : 처리된 메시지 false : 미처리된 메시지

    public void validate() {
        validateStatus();
        validateExpired();
    }
    private void validateStatus() {
        if(this.getStatus()==null) return;
        if(this.getStatus().equals("CANCEL") || this.getStatus().equals("CONFIRMED")) {
            throw new IllegalArgumentException("Invalidate Status");
        }
    }
    private void validateExpired() {
        if(LocalDateTime.now().isAfter(this.expireTime)) {
            throw new IllegalArgumentException("Expired");
        }
    }
}
