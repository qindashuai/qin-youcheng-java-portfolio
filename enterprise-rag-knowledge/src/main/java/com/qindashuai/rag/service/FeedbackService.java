package com.qindashuai.rag.service;

import com.qindashuai.rag.dto.FeedbackDTO;
import com.qindashuai.rag.entity.FeedbackRecord;
import com.qindashuai.rag.vo.FeedbackVO;

import java.util.List;

public interface FeedbackService {

    Long submitFeedback(FeedbackDTO dto);

    FeedbackVO getFeedback(Long id);

    List<FeedbackVO> getFeedbacksByMessageId(Long messageId);

    double getAverageScore(Long knowledgeBaseId);
}
