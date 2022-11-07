package com.team6.onandthefarmmemberservice.kafka;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.team6.onandthefarmmemberservice.dto.MemberPointDto;
import com.team6.onandthefarmmemberservice.repository.UserRepository;
import com.team6.onandthefarmmemberservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PointOrderChannelAdapterKafkaImpl implements PointOrderChannelAdapter {
    private final String TOPIC = "point-order";

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final UserService userService;


    public void producer(String message) {
        this.kafkaTemplate.send(TOPIC, message);
    }

    @KafkaListener(topics = TOPIC,containerFactory = "kafkaListenerContainerFactory")
    public void consumer(String message, Acknowledgment ack) throws Exception {
        log.info(String.format("Message Received : %s", message));
        ObjectMapper objectMapper = new ObjectMapper();
        MemberPointDto memberPointDto = objectMapper.readValue(message, MemberPointDto.class);
        // 포인트 10점
        if(userService.isAlreadyProcessedOrderId(memberPointDto.getOrderSerial())){
            // 중복되지 않은 메시지임으로 결제 생성
            Boolean result = userService.addPoint(memberPointDto.getMemberId());
            if(!result){
                throw new Exception();
            }
        }


        // Kafka Offset Manual Commit(수동커밋)
        ack.acknowledge();
    }
}
