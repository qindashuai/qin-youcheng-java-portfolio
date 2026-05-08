package com.qinyoucheng.rag.service;

import com.qinyoucheng.rag.entity.DocumentChunk;

import java.util.List;

public interface VectorService {

    void embedChunk(DocumentChunk chunk);

    void embedChunksByDocumentId(Long documentId);

    List<DocumentChunk> search(Long knowledgeBaseId, String query, int topK, double threshold);
}
