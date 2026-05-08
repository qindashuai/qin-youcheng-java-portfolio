package com.qinyoucheng.rag.service;

import com.qinyoucheng.rag.common.PageResult;
import com.qinyoucheng.rag.dto.DocumentUploadDTO;
import com.qinyoucheng.rag.entity.KnowledgeDocument;
import com.qinyoucheng.rag.vo.DocumentVO;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

    Long uploadDocument(MultipartFile file, DocumentUploadDTO dto);

    void processDocument(Long documentId);

    DocumentVO getDocument(Long id);

    PageResult<DocumentVO> listDocuments(Long knowledgeBaseId, int pageNum, int pageSize);

    void deleteDocument(Long id);
}
