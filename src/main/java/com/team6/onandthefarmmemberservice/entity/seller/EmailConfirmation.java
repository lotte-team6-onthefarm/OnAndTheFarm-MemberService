package com.team6.onandthefarmmemberservice.entity.seller;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(
        name="EMAIL_CONFIRMATION_SEQ_GENERATOR",
        sequenceName = "EMAIL_CONFIRMATION_SEQ",
        initialValue = 100000, allocationSize = 1
)
public class EmailConfirmation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "EMAIL_CONFIRMATION_SEQ_GENERATOR")
    private long confirmId;
    private String emailId;
    private String authKey;
    private String confirmDate;
}
