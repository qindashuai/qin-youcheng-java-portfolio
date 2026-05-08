package com.qindashuai.rag.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qindashuai.rag.common.BusinessException;
import com.qindashuai.rag.common.ResultCode;
import com.qindashuai.rag.dto.FeedbackDTO;
import com.qindashuai.rag.entity.ChatMessage;
import com.qindashuai.rag.entity.FeedbackRecord;
import com.qindashuai.rag.mapper.ChatMessageMapper;
import com.qindashuai.rag.mapper.FeedbackRecordMapper;
import com.qindashuai.rag.service.FeedbackService;
import com.qindashuai.rag.vo.FeedbackVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRecordMapper feedbackMapper;
    private final ChatMessageMapper messageMapper;

    public FeedbackServiceImpl(FeedbackRecordMapper feedbackMapper,
                               ChatMessageMapper messageMapper) {
        this.feedbackMapper = feedbackMapper;
        this.messageMapper = messageMapper;
    }

    @Override
    public Long submitFeedback(FeedbackDTO dto) {
        ChatMessage message = messageMapper.selectById(dto.getMessageId());
        if (message == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "消息不存在");
        }

        FeedbackRecord record = new FeedbackRecord();
        record.setMessageId(dto.getMessageId());
        record.setConversationId(dto.getConversationId() != null
                ? dto.getConversationId() : message.getConversationId());
        record.setUserId(dto.getUserId());
        record.setScore(dto.getScore());
        record.setComment(dto.getComment());
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        feedbackMapper.insert(record);

        log.info("反馈提交成功: messageId={}, score={}", dto.getMessageId(), dto.getScore());
        return record.getId();
    }

    @Override
    public FeedbackVO getFeedback(Long id) {
        FeedbackRecord record = feedbackMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "反馈记录不存在");
        }
        return convertToVO(record);
    }

    @Override
    public List<FeedbackVO> getFeedbacksByMessageId(Long messageId) {
        LambdaQueryWrapper<FeedbackRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedbackRecord::getMessageId, messageId)
                .orderByDesc(FeedbackRecord::getCreateTime);
        return feedbackMapper.selectList(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public double getAverageScore(Long knowledgeBaseId) {
        LambdaQueryWrapper<FeedbackRecord> wrapper = new LambdaQueryWrapper<>();
        List<FeedbackRecord> records = feedbackMapper.selectList(wrapper);
        if (records.isEmpty()) {
            return 0.0;
        }
        return records.stream()
                .mapToInt(FeedbackRecord::getScore)
                .average()
                .orElse(0.0);
    }

    private FeedbackVO convertToVO(FeedbackRecord record) {
        FeedbackVO vo = new FeedbackVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }
}
