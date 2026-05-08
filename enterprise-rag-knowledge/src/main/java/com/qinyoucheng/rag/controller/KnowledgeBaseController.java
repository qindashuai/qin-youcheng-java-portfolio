package com.qinyoucheng.rag.controller;

import com.qinyoucheng.rag.common.PageResult;
import com.qinyoucheng.rag.common.Result;
import com.qinyoucheng.rag.entity.KnowledgeBase;
import com.qinyoucheng.rag.service.KnowledgeBaseService;
import com.qinyoucheng.rag.vo.KnowledgeBaseVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/knowledge-bases")
public class KnowledgeBaseController {

    private final KnowledgeBaseService knowledgeBaseService;

    public KnowledgeBaseController(KnowledgeBaseService knowledgeBaseService) {
        this.knowledgeBaseService = knowledgeBaseService;
    }

    @PostMapping
    public Result<Long> createKnowledgeBase(@Valid @RequestBody KnowledgeBase knowledgeBase) {
        Long id = knowledgeBaseService.createKnowledgeBase(knowledgeBase);
        return Result.success(id);
    }

    @GetMapping("/{id}")
    public Result<KnowledgeBaseVO> getKnowledgeBase(@PathVariable Long id) {
        KnowledgeBaseVO vo = knowledgeBaseService.getKnowledgeBase(id);
        return Result.success(vo);
    }

    @GetMapping("/list")
    public Result<PageResult<KnowledgeBaseVO>> listKnowledgeBases(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<KnowledgeBaseVO> result = knowledgeBaseService.listKnowledgeBases(category, pageNum, pageSize);
        return Result.success(result);
    }

    @PutMapping
    public Result<Void> updateKnowledgeBase(@Valid @RequestBody KnowledgeBase knowledgeBase) {
        knowledgeBaseService.updateKnowledgeBase(knowledgeBase);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteKnowledgeBase(@PathVariable Long id) {
        knowledgeBaseService.deleteKnowledgeBase(id);
        return Result.success();
    }
}
