package com.qindashuai.rag.service;

import com.qindashuai.rag.common.PageResult;
import com.qindashuai.rag.entity.KnowledgeBase;
import com.qindashuai.rag.vo.KnowledgeBaseVO;

public interface KnowledgeBaseService {

    Long createKnowledgeBase(KnowledgeBase knowledgeBase);

    KnowledgeBaseVO getKnowledgeBase(Long id);

    PageResult<KnowledgeBaseVO> listKnowledgeBases(String category, int pageNum, int pageSize);

    void updateKnowledgeBase(KnowledgeBase knowledgeBase);

    void deleteKnowledgeBase(Long id);
}
