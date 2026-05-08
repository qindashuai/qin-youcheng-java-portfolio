package com.qindashuai.rag.service;

import com.qindashuai.rag.common.PageResult;
import com.qindashuai.rag.dto.DocumentUploadDTO;
import com.qindashuai.rag.entity.KnowledgeDocument;
import com.qindashuai.rag.vo.DocumentVO;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

    Long uploadDocument(MultipartFile file, DocumentUploadDTO dto);

    void processDocument(Long documentId);

    DocumentVO getDocument(Long id);

    PageResult<DocumentVO> listDocuments(Long knowledgeBaseId, int pageNum, int pageSize);

    void deleteDocument(Long id);
}
