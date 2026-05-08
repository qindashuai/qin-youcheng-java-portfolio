package com.qindashuai.rag.service;

import com.qindashuai.rag.entity.DocumentChunk;

import java.util.List;

public interface ChunkService {

    List<DocumentChunk> splitAndSave(Long documentId, String content, String strategy, int chunkSize, int overlap);

    List<DocumentChunk> getChunksByDocumentId(Long documentId);

    List<DocumentChunk> getChunksByKnowledgeBaseId(Long knowledgeBaseId);
}
