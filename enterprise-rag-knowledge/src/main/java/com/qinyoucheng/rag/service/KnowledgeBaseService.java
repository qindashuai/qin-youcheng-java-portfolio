package com.qinyoucheng.rag.service;

import com.qinyoucheng.rag.common.PageResult;
import com.qinyoucheng.rag.entity.KnowledgeBase;
import com.qinyoucheng.rag.vo.KnowledgeBaseVO;

public interface KnowledgeBaseService {

    Long createKnowledgeBase(KnowledgeBase knowledgeBase);

    KnowledgeBaseVO getKnowledgeBase(Long id);

    PageResult<KnowledgeBaseVO> listKnowledgeBases(String category, int pageNum, int pageSize);

    void updateKnowledgeBase(KnowledgeBase knowledgeBase);

    void deleteKnowledgeBase(Long id);
}
