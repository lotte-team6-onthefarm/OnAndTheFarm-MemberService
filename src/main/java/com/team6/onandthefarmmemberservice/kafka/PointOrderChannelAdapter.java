package com.team6.onandthefarmmemberservice.kafka;

import org.springframework.kafka.support.Acknowledgment;

public interface PointOrderChannelAdapter {
    void producer(String message);

    void consumer(String message, Acknowledgment ack) throws Exception;
}
