package com.qinyoucheng.supply.mq;

import com.rabbitmq.client.Channel;
import com.qinyoucheng.supply.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class QualificationWarningConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUALIFICATION_WARNING_QUEUE)
    public void handleWarning(Map<String, Object> warningMsg, Message message, Channel channel) {
        try {
            log.info("收到资质预警消息: {}", warningMsg);

            String supplierName = (String) warningMsg.get("supplierName");
            String qualificationName = (String) warningMsg.get("qualificationName");
            String expireDate = (String) warningMsg.get("expireDate");
            Object daysRemaining = warningMsg.get("daysRemaining");

            log.warn("【资质预警】供应商: {}, 资质: {}, 到期日期: {}, 剩余天数: {}",
                    supplierName, qualificationName, expireDate, daysRemaining);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("处理资质预警消息失败", e);
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (Exception ex) {
                log.error("消息Nack失败", ex);
            }
        }
    }
}
