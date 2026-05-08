package com.qinyoucheng.rag.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qinyoucheng.rag.entity.DocumentChunk;
import com.qinyoucheng.rag.entity.KnowledgeDocument;
import com.qinyoucheng.rag.mapper.DocumentChunkMapper;
import com.qinyoucheng.rag.mapper.KnowledgeDocumentMapper;
import com.qinyoucheng.rag.service.ChunkService;
import com.qinyoucheng.rag.strategy.ChunkStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChunkServiceImpl implements ChunkService {

    private final DocumentChunkMapper chunkMapper;
    private final KnowledgeDocumentMapper documentMapper;
    private final Map<String, ChunkStrategy> strategyMap;

    public ChunkServiceImpl(DocumentChunkMapper chunkMapper,
                            KnowledgeDocumentMapper documentMapper,
                            List<ChunkStrategy> strategies) {
        this.chunkMapper = chunkMapper;
        this.documentMapper = documentMapper;
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(ChunkStrategy::getStrategyName, Function.identity()));
    }

    @Override
    public List<DocumentChunk> splitAndSave(Long documentId, String content, String strategy, int chunkSize, int overlap) {
        ChunkStrategy chunkStrategy = strategyMap.get(strategy);
        if (chunkStrategy == null) {
            chunkStrategy = strategyMap.get("FIXED_SIZE");
        }

        List<String> chunks = chunkStrategy.chunk(content, chunkSize, overlap);
        KnowledgeDocument document = documentMapper.selectById(documentId);
        if (document == null) {
            return new ArrayList<>();
        }

        List<DocumentChunk> result = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            DocumentChunk chunk = new DocumentChunk();
            chunk.setDocumentId(documentId);
            chunk.setKnowledgeBaseId(document.getKnowledgeBaseId());
            chunk.setChunkIndex(i);
            chunk.setContent(chunks.get(i));
            chunk.setTokenCount(estimateTokenCount(chunks.get(i)));
            chunk.setVectorStatus(0);
            chunk.setCreateTime(LocalDateTime.now());
            chunk.setUpdateTime(LocalDateTime.now());
            chunkMapper.insert(chunk);
            result.add(chunk);
        }

        document.setChunkCount(chunks.size());
        documentMapper.updateById(document);

        log.info("文档分块完成: documentId={}, chunkCount={}", documentId, chunks.size());
        return result;
    }

    @Override
    public List<DocumentChunk> getChunksByDocumentId(Long documentId) {
        LambdaQueryWrapper<DocumentChunk> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentChunk::getDocumentId, documentId)
                .orderByAsc(DocumentChunk::getChunkIndex);
        return chunkMapper.selectList(wrapper);
    }

    @Override
    public List<DocumentChunk> getChunksByKnowledgeBaseId(Long knowledgeBaseId) {
        LambdaQueryWrapper<DocumentChunk> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentChunk::getKnowledgeBaseId, knowledgeBaseId)
                .eq(DocumentChunk::getVectorStatus, 1);
        return chunkMapper.selectList(wrapper);
    }

    private int estimateTokenCount(String text) {
        if (text == null) {
            return 0;
        }
        return (int) (text.length() * 0.6);
    }
}
