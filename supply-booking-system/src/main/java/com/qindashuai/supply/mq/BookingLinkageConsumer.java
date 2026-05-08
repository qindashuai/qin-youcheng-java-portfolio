package com.qindashuai.supply.mq;

import cn.hutool.core.util.IdUtil;
import com.rabbitmq.client.Channel;
import com.qindashuai.supply.config.RabbitMQConfig;
import com.qindashuai.supply.entity.ParkEntry;
import com.qindashuai.supply.entity.ReceivingRecord;
import com.qindashuai.supply.mapper.ParkEntryMapper;
import com.qindashuai.supply.mapper.ReceivingRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingLinkageConsumer {

    private final ParkEntryMapper parkEntryMapper;
    private final ReceivingRecordMapper receivingRecordMapper;

    @RabbitListener(queues = RabbitMQConfig.BOOKING_LINKAGE_QUEUE)
    @Transactional(rollbackFor = Exception.class)
    public void handleLinkage(Map<String, Object> linkageMsg, Message message, Channel channel) {
        try {
            log.info("收到预约联动消息: {}", linkageMsg);

            String action = (String) linkageMsg.get("action");
            Long bookingId = toLong(linkageMsg.get("bookingId"));
            Long supplierId = toLong(linkageMsg.get("supplierId"));

            if ("CONFIRMED".equals(action)) {
                createParkEntry(linkageMsg, bookingId, supplierId);
                createReceivingRecord(linkageMsg, bookingId, supplierId);
            }

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("处理预约联动消息失败", e);
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (Exception ex) {
                log.error("消息Nack失败", ex);
            }
        }
    }

    private void createParkEntry(Map<String, Object> msg, Long bookingId, Long supplierId) {
        ParkEntry entry = new ParkEntry();
        entry.setBookingId(bookingId);
        entry.setSupplierId(supplierId);
        entry.setEntryNo("PE" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + IdUtil.simpleUUID().substring(0, 6).toUpperCase());
        entry.setVehicleNo((String) msg.get("vehicleNo"));
        entry.setDriverName((String) msg.get("driverName"));
        entry.setStatus(0);
        parkEntryMapper.insert(entry);
        log.info("自动创建入园登记: bookingId={}, entryNo={}", bookingId, entry.getEntryNo());
    }

    private void createReceivingRecord(Map<String, Object> msg, Long bookingId, Long supplierId) {
        ReceivingRecord record = new ReceivingRecord();
        record.setBookingId(bookingId);
        record.setSupplierId(supplierId);
        record.setReceivingNo("RC" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + IdUtil.simpleUUID().substring(0, 6).toUpperCase());
        record.setQualityStatus(2);
        receivingRecordMapper.insert(record);
        log.info("自动创建收台记录: bookingId={}, receivingNo={}", bookingId, record.getReceivingNo());
    }

    private Long toLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Long) return (Long) obj;
        if (obj instanceof Integer) return ((Integer) obj).longValue();
        return Long.valueOf(obj.toString());
    }
}
