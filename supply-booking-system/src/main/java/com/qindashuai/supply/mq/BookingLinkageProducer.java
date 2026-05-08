package com.qindashuai.supply.mq;

import com.qindashuai.supply.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingLinkageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendLinkageMessage(Map<String, Object> linkageMsg) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BOOKING_LINKAGE_EXCHANGE,
                RabbitMQConfig.BOOKING_LINKAGE_ROUTING_KEY,
                linkageMsg
        );
        log.info("发送预约联动消息: {}", linkageMsg);
    }
}
