package com.team6.onandthefarmmemberservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantLink {
    private URI uri;
    private LocalDateTime expires;
}
