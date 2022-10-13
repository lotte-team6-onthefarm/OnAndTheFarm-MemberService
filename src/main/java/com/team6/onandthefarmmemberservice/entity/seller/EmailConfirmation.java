package com.team6.onandthefarmmemberservice.entity.seller;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long confirmId;

    private String emailId;
    private String authKey;
    private String confirmDate;
}
