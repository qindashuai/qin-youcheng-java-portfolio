package com.qindashuai.rag.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.qindashuai.rag.common.BusinessException;
import com.qindashuai.rag.common.ResultCode;
import com.qindashuai.rag.config.VectorStoreConfig;
import com.qindashuai.rag.entity.DocumentChunk;
import com.qindashuai.rag.mapper.DocumentChunkMapper;
import com.qindashuai.rag.service.VectorService;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.CosineSimilarity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VectorServiceImpl implements VectorService {

    private final DocumentChunkMapper chunkMapper;
    private final EmbeddingModel embeddingModel;
    private final VectorStoreConfig vectorStoreConfig;

    public VectorServiceImpl(DocumentChunkMapper chunkMapper,
                             EmbeddingModel embeddingModel,
                             VectorStoreConfig vectorStoreConfig) {
        this.chunkMapper = chunkMapper;
        this.embeddingModel = embeddingModel;
        this.vectorStoreConfig = vectorStoreConfig;
    }

    @Override
    public void embedChunk(DocumentChunk chunk) {
        try {
            Embedding embedding = embeddingModel.embed(TextSegment.from(chunk.getContent())).content();
            String embeddingJson = JSON.toJSONString(embedding.vector());

            chunk.setEmbedding(embeddingJson);
            chunk.setVectorStatus(1);
            chunk.setUpdateTime(LocalDateTime.now());

            LambdaUpdateWrapper<DocumentChunk> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(DocumentChunk::getId, chunk.getId())
                    .set(DocumentChunk::getEmbedding, embeddingJson)
                    .set(DocumentChunk::getVectorStatus, 1)
                    .set(DocumentChunk::getUpdateTime, LocalDateTime.now());
            chunkMapper.update(null, wrapper);

            log.debug("向量化完成: chunkId={}", chunk.getId());
        } catch (Exception e) {
            log.error("向量化失败: chunkId={}", chunk.getId(), e);
            throw new BusinessException(ResultCode.VECTOR_EMBEDDING_ERROR,
                    "向量化失败: " + e.getMessage());
        }
    }

    @Override
    public void embedChunksByDocumentId(Long documentId) {
        LambdaQueryWrapper<DocumentChunk> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentChunk::getDocumentId, documentId)
                .eq(DocumentChunk::getVectorStatus, 0);
        List<DocumentChunk> chunks = chunkMapper.selectList(wrapper);

        for (DocumentChunk chunk : chunks) {
            try {
                embedChunk(chunk);
            } catch (Exception e) {
                log.error("分块向量化失败: chunkId={}", chunk.getId(), e);
            }
        }

        log.info("文档向量化完成: documentId={}, total={}", documentId, chunks.size());
    }

    @Override
    public List<DocumentChunk> search(Long knowledgeBaseId, String query, int topK, double threshold) {
        try {
            Embedding queryEmbedding = embeddingModel.embed(TextSegment.from(query)).content();
            float[] queryVector = queryEmbedding.vector();

            LambdaQueryWrapper<DocumentChunk> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DocumentChunk::getKnowledgeBaseId, knowledgeBaseId)
                    .eq(DocumentChunk::getVectorStatus, 1);
            List<DocumentChunk> allChunks = chunkMapper.selectList(wrapper);

            List<ScoredChunk> scoredChunks = new ArrayList<>();
            for (DocumentChunk chunk : allChunks) {
                if (chunk.getEmbedding() == null || chunk.getEmbedding().isEmpty()) {
                    continue;
                }
                try {
                    List<Float> chunkVectorList = JSON.parseArray(chunk.getEmbedding(), Float.class);
                    float[] chunkVector = new float[chunkVectorList.size()];
                    for (int i = 0; i < chunkVectorList.size(); i++) {
                        chunkVector[i] = chunkVectorList.get(i);
                    }
                    double similarity = CosineSimilarity.between(queryVector, chunkVector);
                    if (similarity >= threshold) {
                        scoredChunks.add(new ScoredChunk(chunk, similarity));
                    }
                } catch (Exception e) {
                    log.warn("计算相似度失败: chunkId={}", chunk.getId(), e);
                }
            }

            return scoredChunks.stream()
                    .sorted(Comparator.comparingDouble(ScoredChunk::getScore).reversed())
                    .limit(topK)
                    .map(ScoredChunk::getChunk)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("向量检索失败: knowledgeBaseId={}, query={}", knowledgeBaseId, query, e);
            throw new BusinessException(ResultCode.VECTOR_SEARCH_ERROR,
                    "向量检索失败: " + e.getMessage());
        }
    }

    private static class ScoredChunk {
        private final DocumentChunk chunk;
        private final double score;

        ScoredChunk(DocumentChunk chunk, double score) {
            this.chunk = chunk;
            this.score = score;
        }

        DocumentChunk getChunk() { return chunk; }
        double getScore() { return score; }
    }
}
