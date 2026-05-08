package com.qindashuai.rag.service;

import com.qindashuai.rag.entity.DocumentChunk;

import java.util.List;

public interface VectorService {

    void embedChunk(DocumentChunk chunk);

    void embedChunksByDocumentId(Long documentId);

    List<DocumentChunk> search(Long knowledgeBaseId, String query, int topK, double threshold);
}
