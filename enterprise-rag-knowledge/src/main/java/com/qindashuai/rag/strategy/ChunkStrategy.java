package com.qindashuai.rag.strategy;

import java.util.List;

public interface ChunkStrategy {

    List<String> chunk(String content, int chunkSize, int overlap);

    String getStrategyName();
}
