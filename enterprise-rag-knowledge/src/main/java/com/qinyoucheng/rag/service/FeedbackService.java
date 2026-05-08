package com.qinyoucheng.rag.service;

import com.qinyoucheng.rag.dto.FeedbackDTO;
import com.qinyoucheng.rag.entity.FeedbackRecord;
import com.qinyoucheng.rag.vo.FeedbackVO;

import java.util.List;

public interface FeedbackService {

    Long submitFeedback(FeedbackDTO dto);

    FeedbackVO getFeedback(Long id);

    List<FeedbackVO> getFeedbacksByMessageId(Long messageId);

    double getAverageScore(Long knowledgeBaseId);
}
