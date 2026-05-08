package com.qindashuai.rag.controller;

import com.qindashuai.rag.common.Result;
import com.qindashuai.rag.dto.FeedbackDTO;
import com.qindashuai.rag.service.FeedbackService;
import com.qindashuai.rag.vo.FeedbackVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/submit")
    public Result<Long> submitFeedback(@Valid @RequestBody FeedbackDTO dto) {
        Long id = feedbackService.submitFeedback(dto);
        return Result.success(id);
    }

    @GetMapping("/{id}")
    public Result<FeedbackVO> getFeedback(@PathVariable Long id) {
        FeedbackVO vo = feedbackService.getFeedback(id);
        return Result.success(vo);
    }

    @GetMapping("/message/{messageId}")
    public Result<List<FeedbackVO>> getFeedbacksByMessageId(@PathVariable Long messageId) {
        List<FeedbackVO> vos = feedbackService.getFeedbacksByMessageId(messageId);
        return Result.success(vos);
    }

    @GetMapping("/average-score")
    public Result<Double> getAverageScore(@RequestParam(required = false) Long knowledgeBaseId) {
        double score = feedbackService.getAverageScore(knowledgeBaseId);
        return Result.success(score);
    }
}
