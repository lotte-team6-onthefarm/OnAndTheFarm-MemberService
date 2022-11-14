package com.team6.onandthefarmmemberservice.feignclient;

import com.team6.onandthefarmmemberservice.vo.FeedVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "sns-service")
public interface SnsServiceClient {

    @GetMapping("/api/feign/user/feed/sns-service/{feed-no}")
    FeedVo findByFeedNumber(@PathVariable("feed-no") Long feedNumber);
}
